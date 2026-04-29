import React, { useState, useEffect } from 'react';
import { labApi, userApi } from '../services/api';

function LabList() {
  const [labs, setLabs] = useState([]);
  const [users, setUsers] = useState([]);
  const [loading, setLoading] = useState(true);
  const [showModal, setShowModal] = useState(false);
  const [formData, setFormData] = useState({
    name: '',
    code: '',
    safetyLevel: 'LOW',
    responsiblePersonId: '',
    storageCapacity: '',
    usedCapacity: 0,
    availableConditions: ['NORMAL'],
    canReceive: true,
  });

  useEffect(() => {
    loadData();
  }, []);

  const loadData = async () => {
    try {
      const [labsRes, usersRes] = await Promise.all([
        labApi.getAll(),
        userApi.getAll(),
      ]);
      setLabs(labsRes.data);
      setUsers(usersRes.data);
    } catch (error) {
      console.error('Failed to load data:', error);
    } finally {
      setLoading(false);
    }
  };

  const getSafetyLevelBadge = (level) => {
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

  const getStatusBadge = (canReceive) => {
    if (canReceive) {
      return <span className="badge badge-approved">正常</span>;
    }
    return <span className="badge badge-rejected">暂停</span>;
  };

  const getStorageConditionLabel = (conditions) => {
    if (!conditions || conditions.length === 0) return '-';
    const labels = {
      NORMAL: '常温',
      REFRIGERATED: '冷藏',
      FROZEN: '冷冻',
      DARK: '避光',
      VENTILATED: '通风',
      DRY: '干燥',
    };
    return conditions.map(c => labels[c] || c).join(', ');
  };

  const getResponsiblePersonName = (id) => {
    const user = users.find(u => u.id === id);
    return user ? user.name : '-';
  };

  if (loading) {
    return <div className="loading">加载中...</div>;
  }

  return (
    <div>
      <div className="page-header">
        <h2>实验室管理</h2>
      </div>

      <div className="card">
        <table>
          <thead>
            <tr>
              <th>名称</th>
              <th>编号</th>
              <th>安全等级</th>
              <th>负责人</th>
              <th>存储容量</th>
              <th>已用容量</th>
              <th>可用存储条件</th>
              <th>状态</th>
            </tr>
          </thead>
          <tbody>
            {labs.map((lab) => (
              <tr key={lab.id}>
                <td>{lab.name}</td>
                <td>{lab.code}</td>
                <td>{getSafetyLevelBadge(lab.safetyLevel)}</td>
                <td>{getResponsiblePersonName(lab.responsiblePersonId)}</td>
                <td>{lab.storageCapacity}</td>
                <td>{lab.usedCapacity}</td>
                <td>{getStorageConditionLabel(lab.availableConditions)}</td>
                <td>{getStatusBadge(lab.canReceive)}</td>
              </tr>
            ))}
          </tbody>
        </table>
      </div>
    </div>
  );
}

export default LabList;
