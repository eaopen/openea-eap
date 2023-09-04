package org.openea.eap.extj.database.plugins;

import com.baomidou.mybatisplus.core.metadata.TableInfo;
import com.baomidou.mybatisplus.core.metadata.TableInfoHelper;
import com.baomidou.mybatisplus.core.plugins.InterceptorIgnoreHelper;
import com.baomidou.mybatisplus.core.toolkit.ClassUtils;
import com.baomidou.mybatisplus.core.toolkit.CollectionUtils;
import com.baomidou.mybatisplus.core.toolkit.ExceptionUtils;
import com.baomidou.mybatisplus.core.toolkit.PluginUtils;
import com.baomidou.mybatisplus.extension.parser.JsqlParserSupport;
import com.baomidou.mybatisplus.extension.plugins.inner.InnerInterceptor;
import com.baomidou.mybatisplus.extension.toolkit.PropertyMapper;
import net.sf.jsqlparser.JSQLParserException;
import net.sf.jsqlparser.expression.*;
import net.sf.jsqlparser.expression.operators.conditional.AndExpression;
import net.sf.jsqlparser.expression.operators.conditional.OrExpression;
import net.sf.jsqlparser.expression.operators.relational.*;
import net.sf.jsqlparser.parser.CCJSqlParserUtil;
import net.sf.jsqlparser.schema.Column;
import net.sf.jsqlparser.schema.Table;
import net.sf.jsqlparser.statement.Statement;
import net.sf.jsqlparser.statement.delete.Delete;
import net.sf.jsqlparser.statement.insert.Insert;
import net.sf.jsqlparser.statement.select.*;
import net.sf.jsqlparser.statement.update.Update;
import org.apache.ibatis.executor.Executor;
import org.apache.ibatis.executor.statement.StatementHandler;
import org.apache.ibatis.mapping.BoundSql;
import org.apache.ibatis.mapping.MappedStatement;
import org.apache.ibatis.mapping.SqlCommandType;
import org.apache.ibatis.session.ResultHandler;
import org.apache.ibatis.session.RowBounds;
import org.openea.eap.extj.util.StringUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.*;
import java.util.stream.Collectors;

public class MyLogicDeleteInnerInterceptor extends JsqlParserSupport implements InnerInterceptor {

    private static final Logger log = LoggerFactory.getLogger(MyLogicDeleteInnerInterceptor.class);
    private LogicDeleteHandler logicDeleteHandler;
    private static final String LOGIC_DELETE_KEY = "logicdelete";
    private static List<String> tableName = new ArrayList();

    public MyLogicDeleteInnerInterceptor() {
    }

    public void beforeQuery(Executor executor, MappedStatement ms, Object parameter, RowBounds rowBounds, ResultHandler resultHandler, BoundSql boundSql) throws SQLException {
        if (!InterceptorIgnoreHelper.willIgnoreOthersByKey(ms.getId(), "logicdelete")) {
            if (!ms.getId().toLowerCase().contains("ignorelogic")) {
                try {
                    if (boundSql.getSql().toLowerCase().contains(this.logicDeleteHandler.getLogicDeleteColumn().toLowerCase())) {
                        return;
                    }

                    PluginUtils.MPBoundSql mpBs = PluginUtils.mpBoundSql(boundSql);
                    mpBs.sql(this.parserSingle(mpBs.sql(), (Object)null));
                } catch (Exception var8) {
                    if (log.isDebugEnabled()) {
                        log.debug("语句解析失败", var8);
                    }
                }

            }
        }
    }

    public void beforePrepare(StatementHandler sh, Connection connection, Integer transactionTimeout) {
        PluginUtils.MPStatementHandler mpSh = PluginUtils.mpStatementHandler(sh);
        MappedStatement ms = mpSh.mappedStatement();
        if (!ms.getId().toLowerCase().contains("ignorelogic")) {
            SqlCommandType sct = ms.getSqlCommandType();
            if (sct == SqlCommandType.INSERT || sct == SqlCommandType.UPDATE || sct == SqlCommandType.DELETE) {
                if (InterceptorIgnoreHelper.willIgnoreOthersByKey(ms.getId(), "logicdelete")) {
                    return;
                }

                if (mpSh.mPBoundSql().sql().toLowerCase().contains(this.logicDeleteHandler.getLogicDeleteColumn().toLowerCase())) {
                    return;
                }

                try {
                    PluginUtils.MPBoundSql mpBs = mpSh.mPBoundSql();
                    mpBs.sql(this.parserMulti(mpBs.sql(), (Object)null));
                } catch (Exception var8) {
                    if (log.isDebugEnabled()) {
                        log.debug("语句解析失败", var8);
                    }
                }
            }

        }
    }

    protected String processParser(Statement statement, int index, String sql, Object obj) {
        if (this.logger.isDebugEnabled()) {
            this.logger.debug("SQL to parse, SQL: " + sql);
        }

        if (statement instanceof Insert) {
            this.processInsert((Insert)statement, index, sql, obj);
        } else if (statement instanceof Select) {
            this.processSelect((Select)statement, index, sql, obj);
        } else if (statement instanceof Update) {
            this.processUpdate((Update)statement, index, sql, obj);
        } else if (statement instanceof Delete) {
            statement = this.processDeleteToLogicDelete((Delete)statement, index, sql, obj);
        }

        sql = statement.toString();
        if (this.logger.isDebugEnabled()) {
            this.logger.debug("parse the finished SQL: " + sql);
        }

        return sql;
    }

    protected void processInsert(Insert insert, int index, String sql, Object obj) {
        if (!this.ignoreTable(insert.getTable().getName())) {
            List<Column> columns = insert.getColumns();
            if (!CollectionUtils.isEmpty(columns)) {
                String logicDeleteColumn = this.logicDeleteHandler.getLogicDeleteColumn();
                if (!this.logicDeleteHandler.ignoreInsert(columns, logicDeleteColumn)) {
                    columns.add(new Column(logicDeleteColumn));
                    List<Expression> duplicateUpdateColumns = insert.getDuplicateUpdateExpressionList();
                    if (CollectionUtils.isNotEmpty(duplicateUpdateColumns)) {
                        Expression logicExpression = this.getLogicExpression(new StringValue(logicDeleteColumn), this.logicDeleteHandler.getNotDeletedValue());
                        duplicateUpdateColumns.add(logicExpression);
                    }

                    Select select = insert.getSelect();
                    if (select != null) {
                        this.processInsertSelect(select.getSelectBody());
                    } else {
                        if (insert.getItemsList() == null) {
                            throw ExceptionUtils.mpe("Failed to process multiple-table update, please exclude the tableName or statementId", new Object[0]);
                        }

                        ItemsList itemsList = insert.getItemsList();
                        if (itemsList instanceof MultiExpressionList) {
                            ((MultiExpressionList)itemsList).getExpressionLists().forEach((el) -> {
                                el.getExpressions().add(this.logicDeleteHandler.getNotDeletedValue());
                            });
                        } else {
                            ((ExpressionList)itemsList).getExpressions().add(this.logicDeleteHandler.getNotDeletedValue());
                        }
                    }

                }
            }
        }
    }

    protected void processSelect(Select select, int index, String sql, Object obj) {
        this.processSelectBody(select.getSelectBody());
        List<WithItem> withItemsList = select.getWithItemsList();
        if (!CollectionUtils.isEmpty(withItemsList)) {
            withItemsList.forEach(this::processSelectBody);
        }

    }

    protected void processSelectBody(SelectBody selectBody) {
        if (selectBody != null) {
            if (selectBody instanceof PlainSelect) {
                this.processPlainSelect((PlainSelect)selectBody);
            } else if (selectBody instanceof WithItem) {
                WithItem withItem = (WithItem)selectBody;
                this.processSelectBody(withItem.getSubSelect().getSelectBody());
            } else {
                SetOperationList operationList = (SetOperationList)selectBody;
                List<SelectBody> selectBodyList = operationList.getSelects();
                if (CollectionUtils.isNotEmpty(selectBodyList)) {
                    selectBodyList.forEach(this::processSelectBody);
                }
            }

        }
    }

    protected void processUpdate(Update update, int index, String sql, Object obj) {
        Table table = update.getTable();
        if (!this.ignoreTable(table.getName())) {
            update.setWhere(this.andExpression(table, update.getWhere()));
        }
    }

    protected Statement processDeleteToLogicDelete(Delete delete, int index, String sql, Object obj) {
        if (this.ignoreTable(delete.getTable().getName())) {
            return delete;
        } else {
            Update updateStatement = null;

            try {
                updateStatement = (Update) CCJSqlParserUtil.parse(this.logicDeleteHandler.getDeleteSql());
            } catch (JSQLParserException var7) {
                throw new RuntimeException(var7);
            }

            updateStatement.setTable(delete.getTable());
            updateStatement.setWhere(delete.getWhere());
            return updateStatement;
        }
    }

    protected Expression andExpression(Table table, Expression where) {
        Expression logicExpression = this.getLogicExpression(this.getAliasColumn(table), this.logicDeleteHandler.getNotDeletedValue());
        if (null != where) {
            return where instanceof OrExpression ? new AndExpression(logicExpression, new Parenthesis(where)) : new AndExpression(logicExpression, where);
        } else {
            return logicExpression;
        }
    }

    protected void processInsertSelect(SelectBody selectBody) {
        PlainSelect plainSelect = (PlainSelect)selectBody;
        FromItem fromItem = plainSelect.getFromItem();
        if (fromItem instanceof Table) {
            this.processPlainSelect(plainSelect);
            this.appendSelectItem(plainSelect.getSelectItems());
        } else if (fromItem instanceof SubSelect) {
            SubSelect subSelect = (SubSelect)fromItem;
            this.appendSelectItem(plainSelect.getSelectItems());
            this.processInsertSelect(subSelect.getSelectBody());
        }

    }

    protected void appendSelectItem(List<SelectItem> selectItems) {
        if (!CollectionUtils.isEmpty(selectItems)) {
            if (selectItems.size() == 1) {
                SelectItem item = (SelectItem)selectItems.get(0);
                if (item instanceof AllColumns || item instanceof AllTableColumns) {
                    return;
                }
            }

            selectItems.add(new SelectExpressionItem(new Column(this.logicDeleteHandler.getLogicDeleteColumn())));
        }
    }

    protected void processPlainSelect(PlainSelect plainSelect) {
        List<SelectItem> selectItems = plainSelect.getSelectItems();
        if (CollectionUtils.isNotEmpty(selectItems)) {
            selectItems.forEach(this::processSelectItem);
        }

        Expression where = plainSelect.getWhere();
        this.processWhereSubSelect(where);
        FromItem fromItem = plainSelect.getFromItem();
        List<Table> list = this.processFromItem(fromItem);
        List<Table> mainTables = new ArrayList(list);
        List<Join> joins = plainSelect.getJoins();
        if (CollectionUtils.isNotEmpty(joins)) {
            mainTables = this.processJoins((List)mainTables, joins);
        }

        if (CollectionUtils.isNotEmpty((Collection)mainTables)) {
            plainSelect.setWhere(this.builderExpression(where, (List)mainTables));
        }

    }

    private List<Table> processFromItem(FromItem fromItem) {
        while(fromItem instanceof ParenthesisFromItem) {
            fromItem = ((ParenthesisFromItem)fromItem).getFromItem();
        }

        List<Table> mainTables = new ArrayList();
        if (fromItem instanceof Table) {
            Table fromTable = (Table)fromItem;
            mainTables.add(fromTable);
        } else if (fromItem instanceof SubJoin) {
            List<Table> tables = this.processSubJoin((SubJoin)fromItem);
            mainTables.addAll(tables);
        } else {
            this.processOtherFromItem(fromItem);
        }

        return mainTables;
    }

    protected void processWhereSubSelect(Expression where) {
        if (where != null) {
            if (where instanceof FromItem) {
                this.processOtherFromItem((FromItem)where);
            } else {
                if (where.toString().indexOf("SELECT") > 0) {
                    if (where instanceof BinaryExpression) {
                        BinaryExpression expression = (BinaryExpression)where;
                        this.processWhereSubSelect(expression.getLeftExpression());
                        this.processWhereSubSelect(expression.getRightExpression());
                    } else if (where instanceof InExpression) {
                        InExpression expression = (InExpression)where;
                        Expression inExpression = expression.getRightExpression();
                        if (inExpression instanceof SubSelect) {
                            this.processSelectBody(((SubSelect)inExpression).getSelectBody());
                        }
                    } else if (where instanceof ExistsExpression) {
                        ExistsExpression expression = (ExistsExpression)where;
                        this.processWhereSubSelect(expression.getRightExpression());
                    } else if (where instanceof NotExpression) {
                        NotExpression expression = (NotExpression)where;
                        this.processWhereSubSelect(expression.getExpression());
                    } else if (where instanceof Parenthesis) {
                        Parenthesis expression = (Parenthesis)where;
                        this.processWhereSubSelect(expression.getExpression());
                    }
                }

            }
        }
    }

    protected void processSelectItem(SelectItem selectItem) {
        if (selectItem instanceof SelectExpressionItem) {
            SelectExpressionItem selectExpressionItem = (SelectExpressionItem)selectItem;
            if (selectExpressionItem.getExpression() instanceof SubSelect) {
                this.processSelectBody(((SubSelect)selectExpressionItem.getExpression()).getSelectBody());
            } else if (selectExpressionItem.getExpression() instanceof Function) {
                this.processFunction((Function)selectExpressionItem.getExpression());
            }
        }

    }

    protected void processFunction(Function function) {
        ExpressionList parameters = function.getParameters();
        if (parameters != null) {
            parameters.getExpressions().forEach((expression) -> {
                if (expression instanceof SubSelect) {
                    this.processSelectBody(((SubSelect)expression).getSelectBody());
                } else if (expression instanceof Function) {
                    this.processFunction((Function)expression);
                }

            });
        }

    }

    protected void processOtherFromItem(FromItem fromItem) {
        while(fromItem instanceof ParenthesisFromItem) {
            fromItem = ((ParenthesisFromItem)fromItem).getFromItem();
        }

        if (fromItem instanceof SubSelect) {
            SubSelect subSelect = (SubSelect)fromItem;
            if (subSelect.getSelectBody() != null) {
                this.processSelectBody(subSelect.getSelectBody());
            }
        } else if (fromItem instanceof ValuesList) {
            this.logger.debug("Perform a subQuery, if you do not give us feedback");
        } else if (fromItem instanceof LateralSubSelect) {
            LateralSubSelect lateralSubSelect = (LateralSubSelect)fromItem;
            if (lateralSubSelect.getSubSelect() != null) {
                SubSelect subSelect = lateralSubSelect.getSubSelect();
                if (subSelect.getSelectBody() != null) {
                    this.processSelectBody(subSelect.getSelectBody());
                }
            }
        }

    }

    private List<Table> processSubJoin(SubJoin subJoin) {
        List<Table> mainTables = new ArrayList();
        if (subJoin.getJoinList() != null) {
            List<Table> list = this.processFromItem(subJoin.getLeft());
            ((List)mainTables).addAll(list);
            mainTables = this.processJoins((List)mainTables, subJoin.getJoinList());
        }

        return (List)mainTables;
    }

    private List<Table> processJoins(List<Table> mainTables, List<Join> joins) {
        Table mainTable = null;
        Table leftTable = null;
        if (mainTables == null) {
            mainTables = new ArrayList();
        } else if (((List)mainTables).size() == 1) {
            mainTable = (Table)((List)mainTables).get(0);
            leftTable = mainTable;
        }

        Deque<List<Table>> onTableDeque = new LinkedList();
        Iterator var6 = joins.iterator();

        while(true) {
            while(true) {
                while(var6.hasNext()) {
                    Join join = (Join)var6.next();
                    FromItem joinItem = join.getRightItem();
                    List<Table> joinTables = null;
                    if (joinItem instanceof Table) {
                        joinTables = new ArrayList();
                        ((List)joinTables).add((Table)joinItem);
                    } else if (joinItem instanceof SubJoin) {
                        joinTables = this.processSubJoin((SubJoin)joinItem);
                    }

                    if (joinTables != null) {
                        if (join.isSimple()) {
                            ((List)mainTables).addAll((Collection)joinTables);
                        } else {
                            Table joinTable = (Table)((List)joinTables).get(0);
                            List<Table> onTables = null;
                            if (join.isRight()) {
                                mainTable = joinTable;
                                if (leftTable != null) {
                                    onTables = Collections.singletonList(leftTable);
                                }
                            } else if (join.isLeft()) {
                                onTables = Collections.singletonList(joinTable);
                            } else if (join.isInner()) {
                                if (mainTable == null) {
                                    onTables = Collections.singletonList(joinTable);
                                } else {
                                    onTables = Arrays.asList(mainTable, joinTable);
                                }

                                mainTable = null;
                            }

                            mainTables = new ArrayList();
                            if (mainTable != null) {
                                ((List)mainTables).add(mainTable);
                            }

                            Collection<Expression> originOnExpressions = join.getOnExpressions();
                            LinkedList onExpressions;
                            if (originOnExpressions.size() == 1 && onTables != null) {
                                onExpressions = new LinkedList();
                                onExpressions.add(this.builderExpression((Expression)originOnExpressions.iterator().next(), onTables));
                                join.setOnExpressions(onExpressions);
                                leftTable = joinTable;
                            } else {
                                onTableDeque.push(onTables);
                                if (originOnExpressions.size() > 1) {
                                    onExpressions = new LinkedList();
                                    Iterator var14 = originOnExpressions.iterator();

                                    while(var14.hasNext()) {
                                        Expression originOnExpression = (Expression)var14.next();
                                        List<Table> currentTableList = (List)onTableDeque.poll();
                                        if (CollectionUtils.isEmpty(currentTableList)) {
                                            onExpressions.add(originOnExpression);
                                        } else {
                                            onExpressions.add(this.builderExpression(originOnExpression, currentTableList));
                                        }
                                    }

                                    join.setOnExpressions(onExpressions);
                                }

                                leftTable = joinTable;
                            }
                        }
                    } else {
                        this.processOtherFromItem(joinItem);
                        leftTable = null;
                    }
                }

                return (List)mainTables;
            }
        }
    }

    protected Expression builderExpression(Expression currentExpression, List<Table> tables) {
        if (CollectionUtils.isEmpty(tables)) {
            return currentExpression;
        } else {
            List<Table> tempTables = (List)tables.stream().filter((x) -> {
                return !this.ignoreTable(x.getName());
            }).collect(Collectors.toList());
            if (CollectionUtils.isEmpty(tempTables)) {
                return currentExpression;
            } else {
                Expression notDeletedValue = this.logicDeleteHandler.getNotDeletedValue();
                List<Expression> equalsTos = (List)tempTables.stream().map((item) -> {
                    return this.getLogicExpression(this.getAliasColumn(item), notDeletedValue);
                }).collect(Collectors.toList());
                Expression injectExpression = (Expression)equalsTos.get(0);
                if (equalsTos.size() > 1) {
                    for(int i = 1; i < equalsTos.size(); ++i) {
                        injectExpression = new AndExpression((Expression)injectExpression, (Expression)equalsTos.get(i));
                    }
                }

                if (currentExpression == null) {
                    return (Expression)injectExpression;
                } else {
                    return currentExpression instanceof OrExpression ? new AndExpression(new Parenthesis(currentExpression), (Expression)injectExpression) : new AndExpression(currentExpression, (Expression)injectExpression);
                }
            }
        }
    }

    protected Expression getLogicExpression(Expression column, Expression val) {
        if (val.toString().equalsIgnoreCase("null")) {
            IsNullExpression isNullExpression = new IsNullExpression();
            isNullExpression.setLeftExpression(column);
            isNullExpression.setNot(false);
            return isNullExpression;
        } else {
            return new EqualsTo(column, val);
        }
    }

    protected Column getAliasColumn(Table table) {
        StringBuilder column = new StringBuilder();
        if (table.getAlias() != null) {
            column.append(table.getAlias().getName());
        } else {
            column.append(table.getName());
        }

        column.append(".").append(this.logicDeleteHandler.getLogicDeleteColumn());
        return new Column(column.toString());
    }

    private boolean ignoreTable(String table) {
        if (!StringUtil.isEmpty(table) && !this.logicDeleteHandler.ignoreTable(table)) {
            TableInfo tableInfo = TableInfoHelper.getTableInfo(table);
            if (tableInfo != null) {
                return !tableInfo.isWithLogicDelete();
            } else {
                return true;
            }
        } else {
            return true;
        }
    }

    public void setProperties(Properties properties) {
        PropertyMapper.newInstance(properties).whenNotBlank("logicDeleteHandler", ClassUtils::newInstance, this::setLogicDeleteHandler);
    }

    public LogicDeleteHandler getLogicDeleteHandler() {
        return this.logicDeleteHandler;
    }

    public void setLogicDeleteHandler(LogicDeleteHandler logicDeleteHandler) {
        this.logicDeleteHandler = logicDeleteHandler;
    }

}
