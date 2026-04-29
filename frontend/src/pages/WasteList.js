import React, { useState, useEffect } from 'react';
import { wasteApi, chemicalApi, labApi, userApi } from '../services/api';
import { useCurrentUser } from '../App';

function WasteList() {
  const [wasteRecords, setWasteRecords] = useState([]);
  const [recoveryTasks, setRecoveryTasks] = useState([]);
  const [chemicals, setChemicals] = useState([]);
  const [labs, setLabs] = useState([]);
  const [users, setUsers] = useState([]);
  const [loading, setLoading] = useState(true);
  const [showWasteModal, setShowWasteModal] = useState(false);
  const [showApproveModal, setShowApproveModal] = useState(false);
  const [selectedTask, setSelectedTask] = useState(null);
  const [approveData, setApproveData] = useState({ approved: 'true', remark: '' });
  const [formData, setFormData] = useState({
    chemicalId: '',
    userId: '',
    labId: '',
    quantity: '',
    dangerLevel: 'LOW',
    remark: '',
  });

  const { currentUser } = useCurrentUser();

  useEffect(() => {
    loadData();
  }, []);

  const loadData = async () => {
    try {
      const [wasteRes, recoveryRes, chemicalsRes, labsRes, usersRes] = await Promise.all([
        wasteApi.getAllRecords(),
        wasteApi.getAllRecoveryTasks(),
        chemicalApi.getAll(),
        labApi.getAll(),
        userApi.getAll(),
      ]);
      setWasteRecords(wasteRes.data);
      setRecoveryTasks(recoveryRes.data);
      setChemicals(chemicalsRes.data);
      setLabs(labsRes.data);
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

  const getDangerLevelBadge = (level) => {
    const badgeClass = {
      LOW: 'badge-low',
      MEDIUM: 'badge-medium',
      HIGH: 'badge-high',
      EXTREME: 'badge-extreme',
    }[level] || 'badge-low';
    const label = {
      LOW: '低危',
      MEDIUM: '中危',
      HIGH: '高危',
      EXTREME: '极危',
    }[level] || '未知';
    return <span className={`badge ${badgeClass}`}>{label}</span>;
  };

  const getChemicalName = (id) => {
    const chemical = chemicals.find(c => c.id === id);
    return chemical ? chemical.name : '-';
  };

  const getLabName = (id) => {
    const lab = labs.find(l => l.id === id);
    return lab ? lab.name : '-';
  };

  const getUserName = (id) => {
    const user = users.find(u => u.id === id);
    return user ? user.name : '-';
  };

  const handleWasteSubmit = async (e) => {
    e.preventDefault();
    try {
      const data = {
        ...formData,
        quantity: parseFloat(formData.quantity),
      };
      await wasteApi.createRecord(data);
      setShowWasteModal(false);
      setFormData({
        chemicalId: '',
        userId: '',
        labId: '',
        quantity: '',
        dangerLevel: 'LOW',
        remark: '',
      });
      loadData();
    } catch (error) {
      console.error('Failed to record waste:', error);
      alert('登记失败: ' + (error.response?.data?.message || error.message));
    }
  };

  const handleApprove = async () => {
    if (!selectedTask) return;
    if (!currentUser) {
      alert('请先选择当前用户');
      return;
    }
    try {
      await wasteApi.approveRecoveryTask(
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
      <div className="page-header">
        <h2>废弃物管理</h2>
      </div>

      <div className="card">
        <div style={{ display: 'flex', justifyContent: 'space-between', alignItems: 'center', marginBottom: '1rem' }}>
          <h3>废弃物记录</h3>
          <button className="btn btn-primary" onClick={() => setShowWasteModal(true)}>
            + 登记废弃物
          </button>
        </div>
        <table>
          <thead>
            <tr>
              <th>危化品</th>
              <th>重量 (kg)</th>
              <th>危险等级</th>
              <th>登记人</th>
              <th>实验室</th>
              <th>生成时间</th>
              <th>备注</th>
            </tr>
          </thead>
          <tbody>
            {wasteRecords.map((record) => (
              <tr key={record.id}>
                <td>{getChemicalName(record.chemicalId)}</td>
                <td>{record.quantity}</td>
                <td>{getDangerLevelBadge(record.dangerLevel)}</td>
                <td>{getUserName(record.userId)}</td>
                <td>{getLabName(record.labId)}</td>
                <td>{record.generateTime}</td>
                <td>{record.remark || '-'}</td>
              </tr>
            ))}
          </tbody>
        </table>
      </div>

      <div className="card">
        <h3>回收任务</h3>
        <table>
          <thead>
            <tr>
              <th>实验室</th>
              <th>总重量 (kg)</th>
              <th>生成时间</th>
              <th>状态</th>
              <th>审批人</th>
              <th>处理时间</th>
              <th>备注</th>
              <th>操作</th>
            </tr>
          </thead>
          <tbody>
            {recoveryTasks.map((task) => (
              <tr key={task.id}>
                <td>{getLabName(task.labId)}</td>
                <td>{task.totalQuantity}</td>
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

      {showWasteModal && (
        <div className="modal-overlay" onClick={() => setShowWasteModal(false)}>
          <div className="modal" onClick={(e) => e.stopPropagation()}>
            <h3>登记废弃物</h3>
            <form onSubmit={handleWasteSubmit}>
              <div className="form-row">
                <div className="form-group">
                  <label>危化品</label>
                  <select
                    required
                    value={formData.chemicalId}
                    onChange={(e) => setFormData({ ...formData, chemicalId: e.target.value })}
                  >
                    <option value="">请选择</option>
                    {chemicals.map((c) => (
                      <option key={c.id} value={c.id}>{c.name}</option>
                    ))}
                  </select>
                </div>
                <div className="form-group">
                  <label>重量 (kg)</label>
                  <input
                    type="number"
                    step="0.01"
                    required
                    value={formData.quantity}
                    onChange={(e) => setFormData({ ...formData, quantity: e.target.value })}
                  />
                </div>
              </div>
              <div className="form-row">
                <div className="form-group">
                  <label>登记人</label>
                  <select
                    required
                    value={formData.userId}
                    onChange={(e) => setFormData({ ...formData, userId: e.target.value })}
                  >
                    <option value="">请选择</option>
                    {users.map((u) => (
                      <option key={u.id} value={u.id}>{u.name}</option>
                    ))}
                  </select>
                </div>
                <div className="form-group">
                  <label>实验室</label>
                  <select
                    required
                    value={formData.labId}
                    onChange={(e) => setFormData({ ...formData, labId: e.target.value })}
                  >
                    <option value="">请选择</option>
                    {labs.map((l) => (
                      <option key={l.id} value={l.id}>{l.name}</option>
                    ))}
                  </select>
                </div>
              </div>
              <div className="form-group">
                <label>危险等级</label>
                <select
                  value={formData.dangerLevel}
                  onChange={(e) => setFormData({ ...formData, dangerLevel: e.target.value })}
                >
                  <option value="LOW">低危</option>
                  <option value="MEDIUM">中危</option>
                  <option value="HIGH">高危</option>
                  <option value="EXTREME">极危</option>
                </select>
              </div>
              <div className="form-group">
                <label>备注</label>
                <textarea
                  rows="2"
                  value={formData.remark}
                  onChange={(e) => setFormData({ ...formData, remark: e.target.value })}
                />
              </div>
              <div className="modal-footer">
                <button type="button" className="btn" onClick={() => setShowWasteModal(false)}>
                  取消
                </button>
                <button type="submit" className="btn btn-primary">
                  确认登记
                </button>
              </div>
            </form>
          </div>
        </div>
      )}

      {showApproveModal && selectedTask && (
        <div className="modal-overlay" onClick={() => setShowApproveModal(false)}>
          <div className="modal" onClick={(e) => e.stopPropagation()}>
            <h3>审批回收任务</h3>
            <div style={{ marginBottom: '1rem' }}>
              <p><strong>实验室:</strong> {getLabName(selectedTask.labId)}</p>
              <p><strong>总重量:</strong> {selectedTask.totalQuantity} kg</p>
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

export default WasteList;
