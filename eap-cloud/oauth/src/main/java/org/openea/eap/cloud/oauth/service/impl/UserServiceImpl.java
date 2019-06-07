package org.openea.eap.cloud.oauth.service.impl;

import lombok.extern.slf4j.Slf4j;
import org.openea.eap.cloud.oauth.entity.User;
import org.openea.eap.cloud.oauth.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Transactional
@Service
public class UserServiceImpl implements UserDetailsService {

    @Autowired
    private UserRepository userRepository;

    @Transactional(readOnly = true)
    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        User user = userRepository.findByUsername(username);
        if (user != null) {
            user.getRoles().size();
            user.getAuthorities().size();
            return user;
        } else {
            throw new UsernameNotFoundException("User not found!");
        }
    }

}
