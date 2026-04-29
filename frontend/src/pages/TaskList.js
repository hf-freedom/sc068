import React, { useState, useEffect } from 'react';
import { taskApi, chemicalApi, userApi } from '../services/api';
import { useCurrentUser } from '../App';

function TaskList() {
  const [scrapTasks, setScrapTasks] = useState([]);
  const [chemicals, setChemicals] = useState([]);
  const [users, setUsers] = useState([]);
  const [loading, setLoading] = useState(true);
  const [showApproveModal, setShowApproveModal] = useState(false);
  const [selectedTask, setSelectedTask] = useState(null);
  const [approveData, setApproveData] = useState({ approved: 'true', remark: '' });

  const { currentUser } = useCurrentUser();

  useEffect(() => {
    loadData();
  }, []);

  const loadData = async () => {
    try {
      const [scrapRes, chemicalsRes, usersRes] = await Promise.all([
        taskApi.getAllScrapTasks(),
        chemicalApi.getAll(),
        userApi.getAll(),
      ]);
      setScrapTasks(scrapRes.data);
      setChemicals(chemicalsRes.data);
      setUsers(usersRes.data);
    } catch (error) {
      console.error('Failed to load data:', error);
    } finally {
      setLoading(false);
    }
  };

  const getStatusBadge = (status) => {
    const badgeClass = {
      PENDING: 'badge-pending',
      APPROVED: 'badge-approved',
      REJECTED: 'badge-rejected',
      CANCELLED: 'badge-suspended',
    }[status] || 'badge-pending';
    const label = {
      PENDING: '待审批',
      APPROVED: '已通过',
      REJECTED: '已拒绝',
      CANCELLED: '已取消',
    }[status] || '未知';
    return <span className={`badge ${badgeClass}`}>{label}</span>;
  };

  const getChemicalName = (id) => {
    const chemical = chemicals.find(c => c.id === id);
    return chemical ? chemical.name : '-';
  };

  const getUserName = (id) => {
    const user = users.find(u => u.id === id);
    return user ? user.name : '-';
  };

  const handleCheckExpired = async () => {
    try {
      await taskApi.checkExpired();
      alert('已检查过期危化品并生成相应报废任务');
      loadData();
    } catch (error) {
      console.error('Failed to check expired:', error);
      alert('操作失败: ' + (error.response?.data?.message || error.message));
    }
  };

  const handleApprove = async () => {
    if (!selectedTask) return;
    if (!currentUser) {
      alert('请先选择当前用户');
      return;
    }
    try {
      await taskApi.approveScrapTask(
        selectedTask.id,
        currentUser.id,
        approveData.approved === 'true',
        approveData.remark
      );
      setShowApproveModal(false);
      setSelectedTask(null);
      setApproveData({ approved: 'true', remark: '' });
      loadData();
    } catch (error) {
      console.error('Failed to approve:', error);
      alert('操作失败: ' + (error.response?.data?.message || error.message));
    }
  };

  if (loading) {
    return <div className="loading">加载中...</div>;
  }

  return (
    <div>
      <div className="page-header" style={{ display: 'flex', justifyContent: 'space-between', alignItems: 'center' }}>
        <h2>报废任务管理</h2>
        <button className="btn btn-primary" onClick={handleCheckExpired}>
          检查过期危化品
        </button>
      </div>

      <div className="card">
        <h3>报废任务列表</h3>
        <table>
          <thead>
            <tr>
              <th>危化品</th>
              <th>数量</th>
              <th>原因</th>
              <th>生成时间</th>
              <th>状态</th>
              <th>审批人</th>
              <th>处理时间</th>
              <th>备注</th>
              <th>操作</th>
            </tr>
          </thead>
          <tbody>
            {scrapTasks.map((task) => (
              <tr key={task.id}>
                <td>{getChemicalName(task.chemicalId)}</td>
                <td>{task.quantity}</td>
                <td>{task.reason}</td>
                <td>{task.generateTime}</td>
                <td>{getStatusBadge(task.approvalStatus)}</td>
                <td>{getUserName(task.approverId)}</td>
                <td>{task.processTime || '-'}</td>
                <td>{task.remark || '-'}</td>
                <td>
                  {task.approvalStatus === 'PENDING' && (
                    <button
                      className="btn btn-sm btn-primary"
                      onClick={() => {
                        setSelectedTask(task);
                        setShowApproveModal(true);
                      }}
                    >
                      审批
                    </button>
                  )}
                </td>
              </tr>
            ))}
          </tbody>
        </table>
      </div>

      {showApproveModal && selectedTask && (
        <div className="modal-overlay" onClick={() => setShowApproveModal(false)}>
          <div className="modal" onClick={(e) => e.stopPropagation()}>
            <h3>审批报废任务</h3>
            <div style={{ marginBottom: '1rem' }}>
              <p><strong>危化品:</strong> {getChemicalName(selectedTask.chemicalId)}</p>
              <p><strong>数量:</strong> {selectedTask.quantity}</p>
              <p><strong>原因:</strong> {selectedTask.reason}</p>
              <p><strong>当前状态:</strong> {getStatusBadge(selectedTask.approvalStatus)}</p>
              <p><strong>审批人:</strong> {currentUser?.name || '-'}</p>
            </div>
            <div className="form-group">
              <label>审批意见</label>
              <select
                value={approveData.approved}
                onChange={(e) => setApproveData({ ...approveData, approved: e.target.value })}
              >
                <option value="true">通过</option>
                <option value="false">拒绝</option>
              </select>
            </div>
            <div className="form-group">
              <label>备注</label>
              <textarea
                rows="2"
                value={approveData.remark}
                onChange={(e) => setApproveData({ ...approveData, remark: e.target.value })}
              />
            </div>
            <div className="modal-footer">
              <button type="button" className="btn" onClick={() => setShowApproveModal(false)}>
                取消
              </button>
              <button type="button" className="btn btn-primary" onClick={handleApprove}>
                确认
              </button>
            </div>
          </div>
        </div>
      )}
    </div>
  );
}

export default TaskList;
