import React, { useState, useEffect } from 'react';
import { userApi, labApi } from '../services/api';

function UserList() {
  const [users, setUsers] = useState([]);
  const [labs, setLabs] = useState([]);
  const [loading, setLoading] = useState(true);

  useEffect(() => {
    loadData();
  }, []);

  const loadData = async () => {
    try {
      const [usersRes, labsRes] = await Promise.all([
        userApi.getAll(),
        labApi.getAll(),
      ]);
      setUsers(usersRes.data);
      setLabs(labsRes.data);
    } catch (error) {
      console.error('Failed to load data:', error);
    } finally {
      setLoading(false);
    }
  };

  const getStatusBadge = (status) => {
    const badgeClass = {
      ACTIVE: 'badge-active',
      SUSPENDED: 'badge-suspended',
      INACTIVE: 'badge-inactive',
    }[status] || 'badge-suspended';
    const label = {
      ACTIVE: '正常',
      SUSPENDED: '暂停',
      INACTIVE: '停用',
    }[status] || '未知';
    return <span className={`badge ${badgeClass}`}>{label}</span>;
  };

  const getLabName = (labId) => {
    if (!labId) return '-';
    const lab = labs.find(l => l.id === labId);
    return lab ? lab.name : '-';
  };

  const formatList = (list) => {
    if (!list || list.length === 0) return '-';
    return list.join(', ');
  };

  if (loading) {
    return <div className="loading">加载中...</div>;
  }

  return (
    <div>
      <div className="page-header">
        <h2>人员管理</h2>
      </div>

      <div className="card">
        <table>
          <thead>
            <tr>
              <th>姓名</th>
              <th>工号</th>
              <th>所属实验室</th>
              <th>状态</th>
              <th>资质证书</th>
              <th>可领用品类</th>
              <th>单次领用限额</th>
            </tr>
          </thead>
          <tbody>
            {users.map((user) => (
              <tr key={user.id}>
                <td>{user.name}</td>
                <td>{user.employeeId}</td>
                <td>{getLabName(user.labId)}</td>
                <td>{getStatusBadge(user.status)}</td>
                <td>{formatList(user.qualifications)}</td>
                <td>{formatList(user.allowedCategories)}</td>
                <td>{user.singleLimit}</td>
              </tr>
            ))}
          </tbody>
        </table>
      </div>
    </div>
  );
}

export default UserList;
