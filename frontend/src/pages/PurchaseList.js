import React, { useState, useEffect } from 'react';
import { purchaseApi, chemicalApi, labApi, userApi } from '../services/api';
import { useCurrentUser } from '../App';

function PurchaseList() {
  const [purchases, setPurchases] = useState([]);
  const [chemicals, setChemicals] = useState([]);
  const [labs, setLabs] = useState([]);
  const [users, setUsers] = useState([]);
  const [loading, setLoading] = useState(true);
  const [showCreateModal, setShowCreateModal] = useState(false);
  const [showApproveModal, setShowApproveModal] = useState(false);
  const [selectedPurchase, setSelectedPurchase] = useState(null);
  const [approveData, setApproveData] = useState({ approved: 'true', remark: '' });
  const [formData, setFormData] = useState({
    chemicalId: '',
    quantity: '',
    labId: '',
    requesterId: '',
    remark: '',
  });

  const { currentUser } = useCurrentUser();

  useEffect(() => {
    loadData();
  }, []);

  const loadData = async () => {
    try {
      const [purchasesRes, chemicalsRes, labsRes, usersRes] = await Promise.all([
        purchaseApi.getAll(),
        chemicalApi.getAll(),
        labApi.getAll(),
        userApi.getAll(),
      ]);
      setPurchases(purchasesRes.data);
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

  const handleCreateSubmit = async (e) => {
    e.preventDefault();
    try {
      const data = {
        ...formData,
        quantity: parseFloat(formData.quantity),
      };
      await purchaseApi.create(data);
      setShowCreateModal(false);
      setFormData({
        chemicalId: '',
        quantity: '',
        labId: '',
        requesterId: '',
        remark: '',
      });
      loadData();
    } catch (error) {
      console.error('Failed to create purchase:', error);
      alert('创建失败: ' + (error.response?.data?.message || error.message));
    }
  };

  const handleApprove = async () => {
    if (!selectedPurchase) return;
    if (!currentUser) {
      alert('请先选择当前用户');
      return;
    }
    try {
      await purchaseApi.approve(
        selectedPurchase.id,
        currentUser.id,
        approveData.approved === 'true',
        approveData.remark
      );
      setShowApproveModal(false);
      setSelectedPurchase(null);
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
        <h2>采购管理</h2>
        <button className="btn btn-primary" onClick={() => setShowCreateModal(true)}>
          + 新建采购申请
        </button>
      </div>

      <div className="card">
        <table>
          <thead>
            <tr>
              <th>危化品</th>
              <th>数量</th>
              <th>实验室</th>
              <th>申请人</th>
              <th>申请时间</th>
              <th>状态</th>
              <th>一级审批人</th>
              <th>二级审批人</th>
              <th>备注</th>
              <th>操作</th>
            </tr>
          </thead>
          <tbody>
            {purchases.map((purchase) => (
              <tr key={purchase.id}>
                <td>{getChemicalName(purchase.chemicalId)}</td>
                <td>{purchase.quantity}</td>
                <td>{getLabName(purchase.labId)}</td>
                <td>{getUserName(purchase.requesterId)}</td>
                <td>{purchase.requestTime}</td>
                <td>{getStatusBadge(purchase.approvalStatus)}</td>
                <td>{getUserName(purchase.firstApproverId)}</td>
                <td>{getUserName(purchase.secondApproverId)}</td>
                <td>{purchase.remark || '-'}</td>
                <td>
                  {purchase.approvalStatus === 'PENDING' && (
                    <button
                      className="btn btn-sm btn-primary"
                      onClick={() => {
                        setSelectedPurchase(purchase);
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

      {showCreateModal && (
        <div className="modal-overlay" onClick={() => setShowCreateModal(false)}>
          <div className="modal" onClick={(e) => e.stopPropagation()}>
            <h3>新建采购申请</h3>
            <form onSubmit={handleCreateSubmit}>
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
                <label>数量</label>
                <input
                  type="number"
                  step="0.01"
                  required
                  value={formData.quantity}
                  onChange={(e) => setFormData({ ...formData, quantity: e.target.value })}
                />
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
              <div className="form-group">
                <label>申请人</label>
                <select
                  required
                  value={formData.requesterId}
                  onChange={(e) => setFormData({ ...formData, requesterId: e.target.value })}
                >
                  <option value="">请选择</option>
                  {users.map((u) => (
                    <option key={u.id} value={u.id}>{u.name}</option>
                  ))}
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
                <button type="button" className="btn" onClick={() => setShowCreateModal(false)}>
                  取消
                </button>
                <button type="submit" className="btn btn-primary">
                  提交申请
                </button>
              </div>
            </form>
          </div>
        </div>
      )}

      {showApproveModal && selectedPurchase && (
        <div className="modal-overlay" onClick={() => setShowApproveModal(false)}>
          <div className="modal" onClick={(e) => e.stopPropagation()}>
            <h3>审批采购申请</h3>
            <div style={{ marginBottom: '1rem' }}>
              <p><strong>危化品:</strong> {getChemicalName(selectedPurchase.chemicalId)}</p>
              <p><strong>数量:</strong> {selectedPurchase.quantity}</p>
              <p><strong>当前状态:</strong> {getStatusBadge(selectedPurchase.approvalStatus)}</p>
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

export default PurchaseList;
