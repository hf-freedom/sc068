import React, { useState, useEffect } from 'react';
import { safetyCheckApi, labApi, userApi } from '../services/api';

function SafetyCheckList() {
  const [safetyChecks, setSafetyChecks] = useState([]);
  const [labs, setLabs] = useState([]);
  const [users, setUsers] = useState([]);
  const [loading, setLoading] = useState(true);
  const [showModal, setShowModal] = useState(false);
  const [showResumeModal, setShowResumeModal] = useState(false);
  const [resumeData, setResumeData] = useState({ labId: '', reason: '' });
  const [formData, setFormData] = useState({
    labId: '',
    checkerId: '',
    passed: true,
    issues: '',
    remark: '',
  });

  useEffect(() => {
    loadData();
  }, []);

  const loadData = async () => {
    try {
      const [checksRes, labsRes, usersRes] = await Promise.all([
        safetyCheckApi.getAll(),
        labApi.getAll(),
        userApi.getAll(),
      ]);
      setSafetyChecks(checksRes.data);
      setLabs(labsRes.data);
      setUsers(usersRes.data);
    } catch (error) {
      console.error('Failed to load data:', error);
    } finally {
      setLoading(false);
    }
  };

  const getPassedBadge = (passed) => {
    if (passed) {
      return <span className="badge badge-approved">通过</span>;
    }
    return <span className="badge badge-rejected">不通过</span>;
  };

  const getLabName = (id) => {
    const lab = labs.find(l => l.id === id);
    return lab ? lab.name : '-';
  };

  const getUserName = (id) => {
    const user = users.find(u => u.id === id);
    return user ? user.name : '-';
  };

  const getLabStatus = (labId) => {
    const lab = labs.find(l => l.id === labId);
    if (lab && lab.canReceive) {
      return <span className="badge badge-approved">正常</span>;
    }
    return <span className="badge badge-rejected">暂停</span>;
  };

  const handleSubmit = async (e) => {
    e.preventDefault();
    try {
      const data = {
        ...formData,
        issues: formData.issues ? formData.issues.split(',').map(s => s.trim()).filter(Boolean) : [],
      };
      await safetyCheckApi.create(data);
      setShowModal(false);
      setFormData({
        labId: '',
        checkerId: '',
        passed: true,
        issues: '',
        remark: '',
      });
      loadData();
    } catch (error) {
      console.error('Failed to create safety check:', error);
      alert('创建失败: ' + (error.response?.data?.message || error.message));
    }
  };

  const handleResume = async () => {
    try {
      await labApi.resumeAccess(
        resumeData.labId,
        users[0]?.id || 'admin',
        resumeData.reason
      );
      setShowResumeModal(false);
      setResumeData({ labId: '', reason: '' });
      loadData();
    } catch (error) {
      console.error('Failed to resume:', error);
      alert('操作失败: ' + (error.response?.data?.message || error.message));
    }
  };

  if (loading) {
    return <div className="loading">加载中...</div>;
  }

  return (
    <div>
      <div className="page-header" style={{ display: 'flex', justifyContent: 'space-between', alignItems: 'center' }}>
        <h2>安全检查管理</h2>
        <div style={{ display: 'flex', gap: '0.5rem' }}>
          <button className="btn btn-success" onClick={() => setShowResumeModal(true)}>
            恢复实验室权限
          </button>
          <button className="btn btn-primary" onClick={() => setShowModal(true)}>
            + 新增安全检查
          </button>
        </div>
      </div>

      <div className="card">
        <h3>实验室状态</h3>
        <table>
          <thead>
            <tr>
              <th>实验室名称</th>
              <th>当前状态</th>
            </tr>
          </thead>
          <tbody>
            {labs.map((lab) => (
              <tr key={lab.id}>
                <td>{lab.name}</td>
                <td>{getLabStatus(lab.id)}</td>
              </tr>
            ))}
          </tbody>
        </table>
      </div>

      <div className="card">
        <h3>安全检查记录</h3>
        <table>
          <thead>
            <tr>
              <th>实验室</th>
              <th>检查时间</th>
              <th>检查人</th>
              <th>检查结果</th>
              <th>问题</th>
              <th>备注</th>
            </tr>
          </thead>
          <tbody>
            {safetyChecks.map((check) => (
              <tr key={check.id}>
                <td>{getLabName(check.labId)}</td>
                <td>{check.checkTime}</td>
                <td>{getUserName(check.checkerId)}</td>
                <td>{getPassedBadge(check.passed)}</td>
                <td>{check.issues?.join(', ') || '-'}</td>
                <td>{check.remark || '-'}</td>
              </tr>
            ))}
          </tbody>
        </table>
      </div>

      {showModal && (
        <div className="modal-overlay" onClick={() => setShowModal(false)}>
          <div className="modal" onClick={(e) => e.stopPropagation()}>
            <h3>新增安全检查</h3>
            <form onSubmit={handleSubmit}>
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
                  <label>检查人</label>
                  <select
                    required
                    value={formData.checkerId}
                    onChange={(e) => setFormData({ ...formData, checkerId: e.target.value })}
                  >
                    <option value="">请选择</option>
                    {users.map((u) => (
                      <option key={u.id} value={u.id}>{u.name}</option>
                    ))}
                  </select>
                </div>
              </div>
              <div className="form-group">
                <label>检查结果</label>
                <select
                  value={formData.passed}
                  onChange={(e) => setFormData({ ...formData, passed: e.target.value === 'true' })}
                >
                  <option value={true}>通过</option>
                  <option value={false}>不通过</option>
                </select>
              </div>
              <div className="form-group">
                <label>问题（多个问题用逗号分隔）</label>
                <textarea
                  rows="2"
                  value={formData.issues}
                  onChange={(e) => setFormData({ ...formData, issues: e.target.value })}
                />
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
                  确认提交
                </button>
              </div>
            </form>
          </div>
        </div>
      )}

      {showResumeModal && (
        <div className="modal-overlay" onClick={() => setShowResumeModal(false)}>
          <div className="modal" onClick={(e) => e.stopPropagation()}>
            <h3>恢复实验室权限</h3>
            <div className="form-group">
              <label>选择实验室</label>
              <select
                value={resumeData.labId}
                onChange={(e) => setResumeData({ ...resumeData, labId: e.target.value })}
              >
                <option value="">请选择</option>
                {labs.filter(l => !l.canReceive).map((l) => (
                  <option key={l.id} value={l.id}>{l.name}</option>
                ))}
              </select>
            </div>
            <div className="form-group">
              <label>恢复原因</label>
              <textarea
                rows="3"
                value={resumeData.reason}
                onChange={(e) => setResumeData({ ...resumeData, reason: e.target.value })}
              />
            </div>
            <div className="modal-footer">
              <button type="button" className="btn" onClick={() => setShowResumeModal(false)}>
                取消
              </button>
              <button type="button" className="btn btn-success" onClick={handleResume}>
                确认恢复
              </button>
            </div>
          </div>
        </div>
      )}
    </div>
  );
}

export default SafetyCheckList;
