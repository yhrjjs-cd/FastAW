package com.cdyhrj.cloud.approve.service;

import com.cdyhrj.cloud.approve.domain.IdName;
import com.cdyhrj.cloud.approve.service.script.ScriptInstance;
import com.cdyhrj.fastorm.FastORM;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Objects;

/**
 * 选人服务
 *
 * @author <a href="huangqi@cdyhrj.com">黄奇</a>
 */
@Component
@RequiredArgsConstructor
public class SelectPersonService {
    private final FastORM fastORM;
    private static final String SQL_TEXT_BY_ROLE_IDS = "select id, name from app_user where id in (select user_id from app_role_user_allocated where role_id in (:roleIds))";
    private static final String SQL_TEXT_BY_DEPT_ROLE_IDS = "select id, name from app_user where id in (select user_id from app_role_user_allocated where role_id in (:roleIds)) and department_id in (:deptIds)";

    public List<IdName> selectDirectorSuperior() {
        Long userDeptId = UserContextManager.getUserContext().getDeptId();
        Department department = sqlClient.objectQuery()
                .id(userDeptId)
                .fetch(Department.class);

        if (Objects.nonNull(department) && Objects.nonNull(department.getExecutiveId())) {
            User user = sqlClient.objectQuery()
                    .where(Cnd.andEqual(User::getId, department.getExecutiveId()))
                    .fetch(User.class);

            return List.of(IdName.of(user.getId(), user.getName()));
        }

        return Collections.emptyList();
    }

    public List<IdName> selectByRoleIds(List<Long> roleIds) {
        return sqlClient.sqlQuery()
                .sql(SQL_TEXT_BY_ROLE_IDS)
                .param("roleIds", roleIds)
                .query()
                .getValues()
                .stream()
                .map(row -> IdName.of(Long.parseLong(String.valueOf(row.get("id"))), String.valueOf(row.get("name"))))
                .toList();
    }

    public List<IdName> selectDeptAndRoleIds(List<Long> deptIds, List<Long> roleIds) {
        return sqlClient.sqlQuery()
                .sql(SQL_TEXT_BY_DEPT_ROLE_IDS)
                .param("deptIds", deptIds)
                .param("roleIds", roleIds)
                .query()
                .getValues()
                .stream()
                .map(row -> IdName.of(Long.parseLong(String.valueOf(row.get("id"))), String.valueOf(row.get("name"))))
                .toList();
    }

    /**
     * 通过脚本查找审批人
     *
     * @param script     脚本
     * @param submitData 提交数据
     * @return 审批人列表
     */
    public List<IdName> selectByScript(String script, Map<String, Object> submitData) {
        return List.of((IdName) ScriptInstance.execute(script, submitData));
    }
}
