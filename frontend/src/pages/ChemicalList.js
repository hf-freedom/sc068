import React, { useState, useEffect } from 'react';
import { chemicalApi } from '../services/api';

function ChemicalList() {
  const [chemicals, setChemicals] = useState([]);
  const [loading, setLoading] = useState(true);
  const [showModal, setShowModal] = useState(false);
  const [formData, setFormData] = useState({
    name: '',
    casNumber: '',
    dangerLevel: 'LOW',
    storageCondition: 'NORMAL',
    maxStock: '',
    currentStock: 0,
    expiryDate: '',
    unit: 'kg',
    description: '',
  });

  useEffect(() => {
    loadChemicals();
  }, []);

  const loadChemicals = async () => {
    try {
      const response = await chemicalApi.getAll();
      setChemicals(response.data);
    } catch (error) {
      console.error('Failed to load chemicals:', error);
    } finally {
      setLoading(false);
    }
  };

  const handleSubmit = async (e) => {
    e.preventDefault();
    try {
      const data = {
        ...formData,
        maxStock: parseFloat(formData.maxStock),
        currentStock: parseFloat(formData.currentStock) || 0,
      };
      await chemicalApi.create(data);
      setShowModal(false);
      setFormData({
        name: '',
        casNumber: '',
        dangerLevel: 'LOW',
        storageCondition: 'NORMAL',
        maxStock: '',
        currentStock: 0,
        expiryDate: '',
        unit: 'kg',
        description: '',
      });
      loadChemicals();
    } catch (error) {
      console.error('Failed to create chemical:', error);
      alert('创建失败: ' + (error.response?.data?.message || error.message));
    }
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

  const getStorageConditionLabel = (condition) => {
    return {
      NORMAL: '常温',
      REFRIGERATED: '冷藏',
      FROZEN: '冷冻',
      DARK: '避光',
      VENTILATED: '通风',
      DRY: '干燥',
    }[condition] || condition;
  };

  if (loading) {
    return <div className="loading">加载中...</div>;
  }

  return (
    <div>
      <div className="page-header" style={{ display: 'flex', justifyContent: 'space-between', alignItems: 'center' }}>
        <h2>危化品管理</h2>
        <button className="btn btn-primary" onClick={() => setShowModal(true)}>
          + 添加危化品
        </button>
      </div>

      <div className="card">
        <table>
          <thead>
            <tr>
              <th>名称</th>
              <th>CAS号</th>
              <th>危险等级</th>
              <th>存储条件</th>
              <th>当前库存</th>
              <th>最大库存</th>
              <th>有效期</th>
              <th>单位</th>
              <th>描述</th>
            </tr>
          </thead>
          <tbody>
            {chemicals.map((chemical) => (
              <tr key={chemical.id}>
                <td>{chemical.name}</td>
                <td>{chemical.casNumber}</td>
                <td>{getDangerLevelBadge(chemical.dangerLevel)}</td>
                <td>{getStorageConditionLabel(chemical.storageCondition)}</td>
                <td>{chemical.currentStock}</td>
                <td>{chemical.maxStock}</td>
                <td>{chemical.expiryDate}</td>
                <td>{chemical.unit}</td>
                <td>{chemical.description}</td>
              </tr>
            ))}
          </tbody>
        </table>
      </div>

      {showModal && (
        <div className="modal-overlay" onClick={() => setShowModal(false)}>
          <div className="modal" onClick={(e) => e.stopPropagation()}>
            <h3>添加危化品</h3>
            <form onSubmit={handleSubmit}>
              <div className="form-row">
                <div className="form-group">
                  <label>名称</label>
                  <input
                    type="text"
                    required
                    value={formData.name}
                    onChange={(e) => setFormData({ ...formData, name: e.target.value })}
                  />
                </div>
                <div className="form-group">
                  <label>CAS号</label>
                  <input
                    type="text"
                    value={formData.casNumber}
                    onChange={(e) => setFormData({ ...formData, casNumber: e.target.value })}
                  />
                </div>
              </div>
              <div className="form-row">
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
                  <label>存储条件</label>
                  <select
                    value={formData.storageCondition}
                    onChange={(e) => setFormData({ ...formData, storageCondition: e.target.value })}
                  >
                    <option value="NORMAL">常温</option>
                    <option value="REFRIGERATED">冷藏</option>
                    <option value="FROZEN">冷冻</option>
                    <option value="DARK">避光</option>
                    <option value="VENTILATED">通风</option>
                    <option value="DRY">干燥</option>
                  </select>
                </div>
              </div>
              <div className="form-row">
                <div className="form-group">
                  <label>最大库存</label>
                  <input
                    type="number"
                    step="0.01"
                    required
                    value={formData.maxStock}
                    onChange={(e) => setFormData({ ...formData, maxStock: e.target.value })}
                  />
                </div>
                <div className="form-group">
                  <label>当前库存</label>
                  <input
                    type="number"
                    step="0.01"
                    value={formData.currentStock}
                    onChange={(e) => setFormData({ ...formData, currentStock: e.target.value })}
                  />
                </div>
              </div>
              <div className="form-row">
                <div className="form-group">
                  <label>有效期</label>
                  <input
                    type="date"
                    value={formData.expiryDate}
                    onChange={(e) => setFormData({ ...formData, expiryDate: e.target.value })}
                  />
                </div>
                <div className="form-group">
                  <label>单位</label>
                  <select
                    value={formData.unit}
                    onChange={(e) => setFormData({ ...formData, unit: e.target.value })}
                  >
                    <option value="kg">kg</option>
                    <option value="g">g</option>
                    <option value="L">L</option>
                    <option value="mL">mL</option>
                  </select>
                </div>
              </div>
              <div className="form-group">
                <label>描述</label>
                <textarea
                  rows="3"
                  value={formData.description}
                  onChange={(e) => setFormData({ ...formData, description: e.target.value })}
                />
              </div>
              <div className="modal-footer">
                <button type="button" className="btn" onClick={() => setShowModal(false)}>
                  取消
                </button>
                <button type="submit" className="btn btn-primary">
                  确认添加
                </button>
              </div>
            </form>
          </div>
        </div>
      )}
    </div>
  );
}

export default ChemicalList;
