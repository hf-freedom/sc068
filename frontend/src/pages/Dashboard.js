import React from 'react';

function Dashboard({ stats }) {
  const dashboardItems = [
    { label: '危化品种类', count: stats.chemicals, color: '#667eea' },
    { label: '实验室数量', count: stats.labs, color: '#48bb78' },
    { label: '人员数量', count: stats.users, color: '#ed8936' },
    { label: '待审批采购', count: stats.pendingPurchases, color: '#f56565' },
    { label: '待审批领用', count: stats.pendingUsages, color: '#9f7aea' },
    { label: '待处理报废', count: stats.scrapTasks, color: '#ed64a6' },
    { label: '待处理回收', count: stats.recoveryTasks, color: '#38b2ac' },
  ];

  return (
    <div>
      <div className="page-header">
        <h2>仪表盘</h2>
      </div>
      
      <div className="dashboard-grid">
        {dashboardItems.map((item, index) => (
          <div key={index} className="dashboard-card">
            <div className="label">{item.label}</div>
            <div className="count" style={{ color: item.color }}>
              {item.count}
            </div>
          </div>
        ))}
      </div>

      <div className="card" style={{ marginTop: '2rem' }}>
        <h3>系统功能说明</h3>
        <ul style={{ marginTop: '1rem', paddingLeft: '1.5rem' }}>
          <li><strong>采购管理</strong>：创建采购申请，审批时校验实验室容量，高危品需双人审批</li>
          <li><strong>入库管理</strong>：入库时校验存储条件和最大库存</li>
          <li><strong>领用管理</strong>：领用时校验人员资质、单次限额，高危品需双人审批</li>
          <li><strong>废弃物管理</strong>：使用后登记废弃物重量，达到阈值自动生成回收任务</li>
          <li><strong>安全检查</strong>：安全检查不通过时，实验室暂停领用权限</li>
          <li><strong>定时任务</strong>：定时检查过期危化品并生成报废任务</li>
          <li><strong>审计日志</strong>：所有操作均留审计日志</li>
        </ul>
      </div>
    </div>
  );
}

export default Dashboard;
