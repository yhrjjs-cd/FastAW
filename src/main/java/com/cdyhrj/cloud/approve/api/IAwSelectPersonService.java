package com.cdyhrj.cloud.approve.api;

import com.cdyhrj.cloud.approve.domain.IdName;

import java.util.List;
import java.util.Map;

/**
 * 选人服务
 *
 * @author <a href="huangqi@cdyhrj.com">黄奇</a>
 */
public interface IAwSelectPersonService {
    /**
     * 根据当前登录人选择直接上级审批人
     *
     * @param userId 用户Id
     * @return 审批人列表
     */
    List<IdName> selectDirectorSuperior(Long userId);

    /**
     * 通过角色Id选择审批人
     *
     * @param roleIds 角色Id列表
     * @return 审批人列表
     */
    List<IdName> selectByRoleIds(List<Long> roleIds);

    /**
     * 通过部门Id和角色Id选择审批人
     *
     * @param deptIds 部门Id列表
     * @param roleIds 角色Id列表
     * @return 审批人列表
     */
    List<IdName> selectDeptAndRoleIds(List<Long> deptIds, List<Long> roleIds);

    /**
     * 通过脚本查找审批人
     * 可用FastAW提供的实现
     * <pre>{@code
     *     return List.of((IdName) ScriptInstance.execute(script, submitData));
     * }</pre>
     *
     * @param script     脚本
     * @param submitData 提交数据
     * @return 审批人列表
     */
    List<IdName> selectByScript(String script, Map<String, Object> submitData);
}
