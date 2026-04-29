import React, { useState, useEffect } from 'react';
import { stockApi, chemicalApi, labApi, userApi, purchaseApi } from '../services/api';

function StockList() {
  const [stockRecords, setStockRecords] = useState([]);
  const [chemicals, setChemicals] = useState([]);
  const [labs, setLabs] = useState([]);
  const [users, setUsers] = useState([]);
  const [purchases, setPurchases] = useState([]);
  const [loading, setLoading] = useState(true);
  const [showModal, setShowModal] = useState(false);
  const [formData, setFormData] = useState({
    purchaseRequestId: '',
    chemicalId: '',
    labId: '',
    quantity: '',
    operatorId: '',
    batchNumber: '',
    remark: '',
  });

  useEffect(() => {
    loadData();
  }, []);

  const loadData = async () => {
    try {
      const [stockRes, chemicalsRes, labsRes, usersRes, purchasesRes] = await Promise.all([
        stockApi.getAll(),
        chemicalApi.getAll(),
        labApi.getAll(),
        userApi.getAll(),
        purchaseApi.getAll(),
      ]);
      setStockRecords(stockRes.data);
      setChemicals(chemicalsRes.data);
      setLabs(labsRes.data);
      setUsers(usersRes.data);
      setPurchases(purchasesRes.data.filter(p => p.approvalStatus === 'APPROVED'));
    } catch (error) {
      console.error('Failed to load data:', error);
    } finally {
      setLoading(false);
    }
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

  const handleSubmit = async (e) => {
    e.preventDefault();
    try {
      const data = {
        ...formData,
        quantity: parseFloat(formData.quantity),
      };
      await stockApi.stockIn(data);
      setShowModal(false);
      setFormData({
        purchaseRequestId: '',
        chemicalId: '',
        labId: '',
        quantity: '',
        operatorId: '',
        batchNumber: '',
        remark: '',
      });
      loadData();
    } catch (error) {
      console.error('Failed to stock in:', error);
      alert('入库失败: ' + (error.response?.data?.message || error.message));
    }
  };

  if (loading) {
    return <div className="loading">加载中...</div>;
  }

  return (
    <div>
      <div className="page-header" style={{ display: 'flex', justifyContent: 'space-between', alignItems: 'center' }}>
        <h2>入库管理</h2>
        <button className="btn btn-primary" onClick={() => setShowModal(true)}>
          + 新增入库
        </button>
      </div>

      <div className="card">
        <table>
          <thead>
            <tr>
              <th>危化品</th>
              <th>数量</th>
              <th>实验室</th>
              <th>操作员</th>
              <th>入库时间</th>
              <th>批次号</th>
              <th>备注</th>
            </tr>
          </thead>
          <tbody>
            {stockRecords.map((record) => (
              <tr key={record.id}>
                <td>{getChemicalName(record.chemicalId)}</td>
                <td>{record.quantity}</td>
                <td>{getLabName(record.labId)}</td>
                <td>{getUserName(record.operatorId)}</td>
                <td>{record.stockInTime}</td>
                <td>{record.batchNumber || '-'}</td>
                <td>{record.remark || '-'}</td>
              </tr>
            ))}
          </tbody>
        </table>
      </div>

      {showModal && (
        <div className="modal-overlay" onClick={() => setShowModal(false)}>
          <div className="modal" onClick={(e) => e.stopPropagation()}>
            <h3>新增入库</h3>
            <form onSubmit={handleSubmit}>
              <div className="form-group">
                <label>关联采购申请（选填）</label>
                <select
                  value={formData.purchaseRequestId}
                  onChange={(e) => setFormData({ ...formData, purchaseRequestId: e.target.value })}
                >
                  <option value="">无</option>
                  {purchases.map((p) => (
                    <option key={p.id} value={p.id}>
                      {getChemicalName(p.chemicalId)} - {p.quantity}
                    </option>
                  ))}
                </select>
              </div>
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
              <div className="form-row">
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
                  <label>数量</label>
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
                  <label>操作员</label>
                  <select
                    required
                    value={formData.operatorId}
                    onChange={(e) => setFormData({ ...formData, operatorId: e.target.value })}
                  >
                    <option value="">请选择</option>
                    {users.map((u) => (
                      <option key={u.id} value={u.id}>{u.name}</option>
                    ))}
                  </select>
                </div>
                <div className="form-group">
                  <label>批次号</label>
                  <input
                    type="text"
                    value={formData.batchNumber}
                    onChange={(e) => setFormData({ ...formData, batchNumber: e.target.value })}
                  />
                </div>
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
                <button type="button" className="btn" onClick={() => setShowModal(false)}>
                  取消
                </button>
                <button type="submit" className="btn btn-primary">
                  确认入库
                </button>
              </div>
            </form>
          </div>
        </div>
      )}
    </div>
  );
}

export default StockList;
