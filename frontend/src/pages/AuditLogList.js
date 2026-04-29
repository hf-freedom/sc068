import React, { useState, useEffect } from 'react';
import { auditLogApi, userApi } from '../services/api';

function AuditLogList() {
  const [logs, setLogs] = useState([]);
  const [users, setUsers] = useState([]);
  const [loading, setLoading] = useState(true);

  useEffect(() => {
    loadData();
  }, []);

  const loadData = async () => {
    try {
      const [logsRes, usersRes] = await Promise.all([
        auditLogApi.getAll(),
        userApi.getAll(),
      ]);
      setLogs(logsRes.data);
      setUsers(usersRes.data);
    } catch (error) {
      console.error('Failed to load data:', error);
    } finally {
      setLoading(false);
    }
  };

  const getUserName = (id) => {
    if (id === 'system') return '系统';
    const user = users.find(u => u.id === id);
    return user ? user.name : id;
  };

  const getOperationTypeLabel = (type) => {
    const labels = {
      CHEMICAL_CREATE: '创建危化品',
      CHEMICAL_UPDATE: '更新危化品',
      CHEMICAL_DELETE: '删除危化品',
      LAB_CREATE: '创建实验室',
      LAB_UPDATE: '更新实验室',
      LAB_DELETE: '删除实验室',
      USER_CREATE: '创建用户',
      USER_UPDATE: '更新用户',
      USER_DELETE: '删除用户',
      PURCHASE_REQUEST_CREATE: '创建采购申请',
      PURCHASE_REQUEST_APPROVE: '审批采购申请',
      STOCK_IN: '入库',
      USAGE_REQUEST_CREATE: '创建领用申请',
      USAGE_REQUEST_APPROVE: '审批领用申请',
      WASTE_RECORD: '登记废弃物',
      RECOVERY_TASK_CREATE: '创建回收任务',
      RECOVERY_TASK_APPROVE: '审批回收任务',
      SCRAP_TASK_CREATE: '创建报废任务',
      SCRAP_TASK_APPROVED: '报废任务通过',
      SCRAP_TASK_APPROVE: '审批报废任务',
      SAFETY_CHECK_FAILED: '安全检查不通过',
      SAFETY_CHECK_PASSED: '安全检查通过',
      LAB_ACCESS_RESUMED: '恢复实验室权限',
    };
    return labels[type] || type;
  };

  if (loading) {
    return <div className="loading">加载中...</div>;
  }

  return (
    <div>
      <div className="page-header">
        <h2>审计日志</h2>
      </div>

      <div className="card">
        <table>
          <thead>
            <tr>
              <th>操作时间</th>
              <th>操作类型</th>
              <th>操作人</th>
              <th>关联ID</th>
              <th>详情</th>
              <th>IP</th>
            </tr>
          </thead>
          <tbody>
            {logs.map((log) => (
              <tr key={log.id}>
                <td>{log.operationTime}</td>
                <td>
                  <span className="badge badge-medium">
                    {getOperationTypeLabel(log.operationType)}
                  </span>
                </td>
                <td>{getUserName(log.userId)}</td>
                <td>{log.relatedId || '-'}</td>
                <td>{log.detail || '-'}</td>
                <td>{log.ip || '-'}</td>
              </tr>
            ))}
          </tbody>
        </table>
        {logs.length === 0 && (
          <div className="loading">暂无审计日志</div>
        )}
      </div>
    </div>
  );
}

export default AuditLogList;
