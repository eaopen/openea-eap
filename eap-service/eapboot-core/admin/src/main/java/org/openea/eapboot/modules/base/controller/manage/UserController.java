package org.openea.eapboot.modules.base.controller.manage;

import org.openea.eapboot.common.annotation.SystemLog;
import org.openea.eapboot.common.exception.EapbootException;
import org.openea.eapboot.modules.base.aync.AddMessage;
import org.openea.eapboot.common.constant.CommonConstant;
import org.openea.eapboot.common.enums.LogType;
import org.openea.eapboot.common.utils.PageUtil;
import org.openea.eapboot.common.utils.ResultUtil;
import org.openea.eapboot.common.utils.SecurityUtil;
import org.openea.eapboot.common.vo.PageVo;
import org.openea.eapboot.common.vo.Result;
import org.openea.eapboot.common.vo.SearchVo;
//import org.openea.eapboot.modules.activiti.service.ActNodeService;
import org.openea.eapboot.modules.base.entity.Department;
import org.openea.eapboot.modules.base.entity.Role;
import org.openea.eapboot.modules.base.entity.User;
import org.openea.eapboot.modules.base.entity.UserRole;
import org.openea.eapboot.modules.base.service.*;
import org.openea.eapboot.modules.base.service.mybatis.IUserRoleService;
import cn.hutool.core.util.StrUtil;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CacheConfig;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.data.domain.Page;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;


/**
 */
@Slf4j
@RestController
@Api(description = "用户接口")
@RequestMapping("/eapboot/user")
@CacheConfig(cacheNames = "user")
@Transactional
public class UserController {

    @Autowired
    private UserService userService;

    @Autowired
    private RoleService roleService;

    @Autowired
    private DepartmentService departmentService;

    @Autowired
    private DepartmentHeaderService departmentHeaderService;

    @Autowired
    private IUserRoleService iUserRoleService;

    @Autowired
    private UserRoleService userRoleService;

    @Autowired
    private AddMessage addMessage;

    //@Autowired
    //private ActNodeService actNodeService;

//    @Autowired
//    private QQService qqService;
//
//    @Autowired
//    private WeiboService weiboService;
//
//    @Autowired
//    private GithubService githubService;

    @Autowired
    private StringRedisTemplate redisTemplate;

    @Autowired
    private SecurityUtil securityUtil;

    @PersistenceContext
    private EntityManager entityManager;

    @RequestMapping(value = "/smsLogin", method = RequestMethod.POST)
    @SystemLog(description = "短信登录", type = LogType.LOGIN)
    @ApiOperation(value = "短信登录接口")
    public Result<Object> smsLogin(@RequestParam String mobile,
                                   @RequestParam String code,
                                   @RequestParam(required = false) Boolean saveLogin){

        // 验证短信验证码
        String v = redisTemplate.opsForValue().get(CommonConstant.PRE_SMS + mobile);
        if(StrUtil.isBlank(v)){
            throw new EapbootException("验证码失效或KEY不正确");
        }
        if(!code.equals(v)){
            throw new EapbootException("验证码不正确");
        }
        User u = userService.findByMobile(mobile);
        if(u==null){
            throw new EapbootException("手机号不存在");
        }
        String accessToken = securityUtil.getToken(u.getUsername(), saveLogin);
        // 已验证 清除key
        redisTemplate.delete(CommonConstant.PRE_SMS + mobile);
        return new ResultUtil<Object>().setData(accessToken);
    }

    @RequestMapping(value = "/resetByMobile", method = RequestMethod.POST)
    @ApiOperation(value = "通过短信重置密码")
    public Result<Object> resetByMobile(@RequestParam String mobile,
                                        @RequestParam String code,
                                        @RequestParam String password,
                                        @RequestParam String passStrength){

        // 验证短信验证码
        String v = redisTemplate.opsForValue().get(CommonConstant.PRE_SMS + mobile);
        if(StrUtil.isBlank(v)){
            return new ResultUtil<Object>().setErrorMsg("验证码失效或KEY不正确");
        }
        if(!code.equals(v)){
            return new ResultUtil<Object>().setErrorMsg("验证码不正确");
        }
        User u = userService.findByMobile(mobile);
        String encryptPass= new BCryptPasswordEncoder().encode(password);
        u.setPassword(encryptPass);
        u.setPassStrength(passStrength);
        userService.update(u);
        // 删除缓存
        redisTemplate.delete("user::"+u.getUsername());
        // 已验证清除key
        redisTemplate.delete(CommonConstant.PRE_SMS + mobile);
        return new ResultUtil<Object>().setSuccessMsg("重置密码成功");
    }

    /**
     * 在线demo所需 未使用短信验证码
     * @param u
     * @param verify
     * @param captchaId
     * @return
     */
    @RequestMapping(value = "/regist", method = RequestMethod.POST)
    @ApiOperation(value = "注册用户")
    public Result<Object> regist(@ModelAttribute User u,
                                 @RequestParam String verify,
                                 @RequestParam String captchaId){

        if(StrUtil.isBlank(verify)|| StrUtil.isBlank(u.getUsername())
                || StrUtil.isBlank(u.getPassword())){
            return new ResultUtil<Object>().setErrorMsg("缺少必需表单字段");
        }

        // 图形验证码
        String code=redisTemplate.opsForValue().get(captchaId);
        if(StrUtil.isBlank(code)){
            return new ResultUtil<Object>().setErrorMsg("验证码已过期，请重新获取");
        }

        if(!verify.toLowerCase().equals(code.toLowerCase())) {
            log.error("注册失败，验证码错误：code:"+ verify +",redisCode:"+code.toLowerCase());
            return new ResultUtil<Object>().setErrorMsg("验证码输入错误");
        }

        if(userService.findByUsername(u.getUsername())!=null){
            return new ResultUtil<Object>().setErrorMsg("该用户名已被注册");
        }
        // 删除缓存
        redisTemplate.delete("user::"+u.getUsername());

        if(userService.findByMobile(u.getMobile())!=null){
            return new ResultUtil<Object>().setErrorMsg("该手机号已被注册");
        }

        String encryptPass = new BCryptPasswordEncoder().encode(u.getPassword());
        u.setPassword(encryptPass);
        u.setType(CommonConstant.USER_TYPE_NORMAL);
        User user = userService.save(u);
        if(user==null){
            return new ResultUtil<Object>().setErrorMsg("注册失败");
        }
        // 默认角色
        List<Role> roleList = roleService.findByDefaultRole(true);
        if(roleList!=null&&roleList.size()>0){
            for(Role role : roleList){
                UserRole ur = new UserRole();
                ur.setUserId(user.getId());
                ur.setRoleId(role.getId());
                iUserRoleService.save(ur);
            }
        }
        // 异步发送创建账号消息
        addMessage.addSendMessage(user.getId());

        return new ResultUtil<Object>().setData(user);
    }

    @RequestMapping(value = "/info", method = RequestMethod.GET)
    @ApiOperation(value = "获取当前登录用户接口")
    public Result<User> getUserInfo(){

        User u = securityUtil.getCurrUser();
        // 清除持久上下文环境 避免后面语句导致持久化
        entityManager.clear();
        u.setPassword(null);
        return new ResultUtil<User>().setData(u);
    }

    @RequestMapping(value = "/changeMobile", method = RequestMethod.POST)
    @ApiOperation(value = "修改绑定手机")
    public Result<Object> changeMobile(@RequestParam String mobile,
                                       @RequestParam String code){

        // 验证短信验证码
        String v = redisTemplate.opsForValue().get(CommonConstant.PRE_SMS + mobile);
        if(StrUtil.isBlank(v)){
            return new ResultUtil<Object>().setErrorMsg("验证码失效或KEY不正确");
        }
        if(!code.equals(v)){
            return new ResultUtil<Object>().setErrorMsg("验证码不正确");
        }
        if(userService.findByMobile(mobile)!=null){
            return new ResultUtil<Object>().setErrorMsg("该手机号已绑定其他账户");
        }
        User u = securityUtil.getCurrUser();
        u.setMobile(mobile);
        userService.update(u);
        // 删除缓存
        redisTemplate.delete("user::"+u.getUsername());
        // 已验证清除key
        redisTemplate.delete(CommonConstant.PRE_SMS + mobile);
        return new ResultUtil<Object>().setSuccessMsg("修改手机号成功");
    }

    @RequestMapping(value = "/unlock", method = RequestMethod.POST)
    @ApiOperation(value = "解锁验证密码")
    public Result<Object> unLock(@RequestParam String password){

        User u = securityUtil.getCurrUser();
        if(!new BCryptPasswordEncoder().matches(password, u.getPassword())){
            return new ResultUtil<Object>().setErrorMsg("密码不正确");
        }
        return new ResultUtil<Object>().setData(null);
    }

    @RequestMapping(value = "/edit", method = RequestMethod.POST)
    @ApiOperation(value = "修改用户自己资料",notes = "用户名密码不会修改 需要username更新缓存")
    @CacheEvict(key = "#u.username")
    public Result<Object> editOwn(@ModelAttribute User u){

        User old = securityUtil.getCurrUser();
        u.setUsername(old.getUsername());
        u.setPassword(old.getPassword());
        User user = userService.update(u);
        if(user==null){
            return new ResultUtil<Object>().setErrorMsg("修改失败");
        }
        return new ResultUtil<Object>().setSuccessMsg("修改成功");
    }

    @RequestMapping(value = "/admin/edit", method = RequestMethod.POST)
    @ApiOperation(value = "管理员修改资料",notes = "需要通过id获取原用户信息 需要username更新缓存")
    @CacheEvict(key = "#u.username")
    public Result<Object> edit(@ModelAttribute User u,
                               @RequestParam(required = false) String[] roles){

        User old = userService.get(u.getId());
        //若修改了用户名
        if(!old.getUsername().equals(u.getUsername())){
            //若修改用户名删除原用户名缓存
            redisTemplate.delete("user::"+old.getUsername());
            //判断新用户名是否存在
            if(userService.findByUsername(u.getUsername())!=null){
                return new ResultUtil<Object>().setErrorMsg("该用户名已存在");
            }
            //删除缓存
            redisTemplate.delete("user::"+u.getUsername());
        }

        // 若修改了手机和邮箱判断是否唯一
        if(!old.getMobile().equals(u.getMobile())&&userService.findByMobile(u.getMobile())!=null){
            return new ResultUtil<Object>().setErrorMsg("该手机号已绑定其他账户");
        }
        if(!old.getEmail().equals(u.getEmail())&&userService.findByMobile(u.getEmail())!=null){
            return new ResultUtil<Object>().setErrorMsg("该邮箱已绑定其他账户");
        }

        u.setPassword(old.getPassword());
        User user = userService.update(u);
        if(user==null){
            return new ResultUtil<Object>().setErrorMsg("修改失败");
        }
        //删除该用户角色
        userRoleService.deleteByUserId(u.getId());
        if(roles!=null&&roles.length>0){
            //新角色
            for(String roleId : roles){
                UserRole ur = new UserRole();
                ur.setRoleId(roleId);
                ur.setUserId(u.getId());
                userRoleService.save(ur);
            }
        }
        //手动删除缓存
        redisTemplate.delete("userRole::"+u.getId());
        redisTemplate.delete("userRole::depIds:"+u.getId());
        return new ResultUtil<Object>().setSuccessMsg("修改成功");
    }

    /**
     * 线上demo不允许测试账号改密码
     * @param password
     * @param newPass
     * @return
     */
    @RequestMapping(value = "/modifyPass", method = RequestMethod.POST)
    @ApiOperation(value = "修改密码")
    public Result<Object> modifyPass(@ApiParam("旧密码") @RequestParam String password,
                                     @ApiParam("新密码") @RequestParam String newPass,
                                     @ApiParam("密码强度") @RequestParam String passStrength){

        User user = securityUtil.getCurrUser();
        //在线DEMO所需
        if("test".equals(user.getUsername())||"test2".equals(user.getUsername())){
            return new ResultUtil<Object>().setErrorMsg("演示账号不支持修改密码");
        }

        if(!new BCryptPasswordEncoder().matches(password, user.getPassword())){
            return new ResultUtil<Object>().setErrorMsg("旧密码不正确");
        }

        String newEncryptPass= new BCryptPasswordEncoder().encode(newPass);
        user.setPassword(newEncryptPass);
        user.setPassStrength(passStrength);
        userService.update(user);

        //手动更新缓存
        redisTemplate.delete("user::"+user.getUsername());

        return new ResultUtil<Object>().setSuccessMsg("修改密码成功");
    }

    @RequestMapping(value = "/getByCondition", method = RequestMethod.GET)
    @ApiOperation(value = "多条件分页获取用户列表")
    public Result<Page<User>> getByCondition(@ModelAttribute User user,
                                             @ModelAttribute SearchVo searchVo,
                                             @ModelAttribute PageVo pageVo){

        Page<User> page = userService.findByCondition(user, searchVo, PageUtil.initPage(pageVo));
        for(User u: page.getContent()){
            // 关联部门
            if(StrUtil.isNotBlank(u.getDepartmentId())){
                Department department = departmentService.get(u.getDepartmentId());
                u.setDepartmentTitle(department.getTitle());
            }
            // 关联角色
            List<Role> list = iUserRoleService.findByUserId(u.getId());
            u.setRoles(list);
            // 清除持久上下文环境 避免后面语句导致持久化
            entityManager.clear();
            u.setPassword(null);
        }
        return new ResultUtil<Page<User>>().setData(page);
    }

    @RequestMapping(value = "/getByDepartmentId/{departmentId}", method = RequestMethod.GET)
    @ApiOperation(value = "多条件分页获取用户列表")
    public Result<List<User>> getByCondition(@PathVariable String departmentId){

        List<User> list = userService.findByDepartmentId(departmentId);
        entityManager.clear();
        list.forEach(u -> {
            u.setPassword(null);
        });
        return new ResultUtil<List<User>>().setData(list);
    }

    @RequestMapping(value = "/searchByName/{username}", method = RequestMethod.GET)
    @ApiOperation(value = "通过用户名搜索用户")
    public Result<List<User>> searchByName(@PathVariable String username) throws UnsupportedEncodingException {

        List<User> list = userService.findByUsernameLikeAndStatus("%"+ URLDecoder.decode(username, "utf-8")+"%", CommonConstant.STATUS_NORMAL);
        return new ResultUtil<List<User>>().setData(list);
    }

    @RequestMapping(value = "/getAll", method = RequestMethod.GET)
    @ApiOperation(value = "获取全部用户数据")
    public Result<List<User>> getByCondition(){

        List<User> list = userService.getAll();
        for(User u: list){
            // 关联部门
            if(StrUtil.isNotBlank(u.getDepartmentId())){
                Department department = departmentService.get(u.getDepartmentId());
                u.setDepartmentTitle(department.getTitle());
            }
            // 清除持久上下文环境 避免后面语句导致持久化
            entityManager.clear();
            u.setPassword(null);
        }
        return new ResultUtil<List<User>>().setData(list);
    }

    @RequestMapping(value = "/admin/add", method = RequestMethod.POST)
    @ApiOperation(value = "添加用户")
    public Result<Object> regist(@ModelAttribute User u,
                                 @RequestParam(required = false) String[] roles){

        if(StrUtil.isBlank(u.getUsername()) || StrUtil.isBlank(u.getPassword())){
            return new ResultUtil<Object>().setErrorMsg("缺少必需表单字段");
        }

        if(userService.findByUsername(u.getUsername())!=null){
            return new ResultUtil<Object>().setErrorMsg("该用户名已被注册");
        }
        //删除缓存
        redisTemplate.delete("user::"+u.getUsername());

        String encryptPass = new BCryptPasswordEncoder().encode(u.getPassword());
        u.setPassword(encryptPass);
        User user = userService.save(u);
        if(user==null){
            return new ResultUtil<Object>().setErrorMsg("添加失败");
        }
        if(roles!=null&&roles.length>0){
            //添加角色
            for(String roleId : roles){
                UserRole ur = new UserRole();
                ur.setUserId(u.getId());
                ur.setRoleId(roleId);
                userRoleService.save(ur);
            }
        }
        // 发送创建账号消息
        addMessage.addSendMessage(user.getId());

        return new ResultUtil<Object>().setSuccessMsg("添加成功");
    }

    @RequestMapping(value = "/admin/disable/{userId}", method = RequestMethod.POST)
    @ApiOperation(value = "后台禁用用户")
    public Result<Object> disable(@ApiParam("用户唯一id标识") @PathVariable String userId){

        User user = userService.get(userId);
        if(user==null){
            return new ResultUtil<Object>().setErrorMsg("通过userId获取用户失败");
        }
        user.setStatus(CommonConstant.USER_STATUS_LOCK);
        userService.update(user);
        //手动更新缓存
        redisTemplate.delete("user::"+user.getUsername());
        return new ResultUtil<Object>().setSuccessMsg("操作成功");
    }

    @RequestMapping(value = "/admin/enable/{userId}", method = RequestMethod.POST)
    @ApiOperation(value = "后台启用用户")
    public Result<Object> enable(@ApiParam("用户唯一id标识") @PathVariable String userId){

        User user = userService.get(userId);
        if(user==null){
            return new ResultUtil<Object>().setErrorMsg("通过userId获取用户失败");
        }
        user.setStatus(CommonConstant.USER_STATUS_NORMAL);
        userService.update(user);
        //手动更新缓存
        redisTemplate.delete("user::"+user.getUsername());
        return new ResultUtil<Object>().setSuccessMsg("操作成功");
    }

    @RequestMapping(value = "/delByIds/{ids}", method = RequestMethod.DELETE)
    @ApiOperation(value = "批量通过ids删除")
    public Result<Object> delAllByIds(@PathVariable String[] ids){

        for(String id:ids){
            User u = userService.get(id);
            //删除相关缓存
            redisTemplate.delete("user::" + u.getUsername());
            redisTemplate.delete("userRole::" + u.getId());
            redisTemplate.delete("userRole::depIds:" + u.getId());
            redisTemplate.delete("permission::userMenuList:" + u.getId());
            Set<String> keys = redisTemplate.keys("department::*");
            redisTemplate.delete(keys);

            //TODO 需要解除耦合
            // 删除关联社交账号
//            qqService.deleteByUsername(u.getUsername());
//            weiboService.deleteByUsername(u.getUsername());
//            githubService.deleteByUsername(u.getUsername());

            userService.delete(id);

            //删除关联角色
            userRoleService.deleteByUserId(id);
            // 删除关联部门负责人
            departmentHeaderService.deleteByUserId(id);
            // TODO 关联工作流
            // 删除流程关联节点
            //actNodeService.deleteByRelateId(id);
        }
        return new ResultUtil<Object>().setSuccessMsg("批量通过id删除数据成功");
    }

    @RequestMapping(value = "/importData", method = RequestMethod.POST)
    @ApiOperation(value = "导入用户数据")
    public Result<Object> importData(@RequestBody List<User> users){

        List<Integer> errors = new ArrayList<>();
        List<String> reasons = new ArrayList<>();
        int count = 0;
        for(User u: users){
            count++;
            // 验证用户名密码不为空
            if(StrUtil.isBlank(u.getUsername())||StrUtil.isBlank(u.getPassword())){
                errors.add(count);
                reasons.add("用户名或密码为空");
                continue;
            }
            // 验证用户名唯一
            if(userService.findByUsername(u.getUsername())!=null){
                errors.add(count);
                reasons.add("用户名已存在");
                continue;
            }
            //删除缓存
            redisTemplate.delete("user::"+u.getUsername());
            // 加密密码
            u.setPassword(new BCryptPasswordEncoder().encode(u.getPassword()));
            // 验证部门id正确性
            if(StrUtil.isNotBlank(u.getDepartmentId())){
                try {
                    Department d = departmentService.get(u.getDepartmentId());
                    log.info(d.toString());
                }catch (Exception e){
                    errors.add(count);
                    reasons.add("部门id不存在");
                    continue;
                }
            }
            if(u.getStatus()==null){
                u.setStatus(CommonConstant.USER_STATUS_NORMAL);
            }
            // 分配默认角色
            if(u.getDefaultRole()!=null&&u.getDefaultRole()==1){
                List<Role> roleList = roleService.findByDefaultRole(true);
                if(roleList!=null&&roleList.size()>0){
                    for(Role role : roleList){
                        UserRole ur = new UserRole();
                        ur.setUserId(u.getId());
                        ur.setRoleId(role.getId());
                        iUserRoleService.save(ur);
                    }
                }
            }
            // 保存数据
            userService.save(u);
        }
        int successCount = users.size() - errors.size();
        String successMessage = "全部导入成功，共计 " + successCount + " 条数据";
        String failMessage = "导入成功 " + successCount + " 条，失败 " + errors.size() + " 条数据。<br>" +
                "第 " + errors.toString() + " 行数据导入出错，错误原因分别为：<br>" + reasons.toString();
        String message = "";
        if(errors.size()==0){
            message = successMessage;
        }else{
            message = failMessage;
        }
        return new ResultUtil<Object>().setSuccessMsg(message);
    }
}
