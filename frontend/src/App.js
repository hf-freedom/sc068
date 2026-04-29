import React, { useState, useEffect, createContext, useContext } from 'react';
import { BrowserRouter as Router, Routes, Route, NavLink } from 'react-router-dom';
import Dashboard from './pages/Dashboard';
import ChemicalList from './pages/ChemicalList';
import LabList from './pages/LabList';
import UserList from './pages/UserList';
import PurchaseList from './pages/PurchaseList';
import StockList from './pages/StockList';
import UsageList from './pages/UsageList';
import WasteList from './pages/WasteList';
import TaskList from './pages/TaskList';
import SafetyCheckList from './pages/SafetyCheckList';
import AuditLogList from './pages/AuditLogList';
import { chemicalApi, labApi, userApi, purchaseApi, usageApi, taskApi, wasteApi } from './services/api';

const UserContext = createContext(null);

export const useCurrentUser = () => {
  const context = useContext(UserContext);
  if (!context) {
    throw new Error('useCurrentUser must be used within a UserProvider');
  }
  return context;
};

function App() {
  const [stats, setStats] = useState({
    chemicals: 0,
    labs: 0,
    users: 0,
    pendingPurchases: 0,
    pendingUsages: 0,
    scrapTasks: 0,
    recoveryTasks: 0,
  });

  const [allUsers, setAllUsers] = useState([]);
  const [currentUser, setCurrentUser] = useState(null);

  useEffect(() => {
    loadData();
  }, []);

  const loadData = async () => {
    try {
      const [chemicalsRes, labsRes, usersRes, purchasesRes, usagesRes, scrapRes, recoveryRes] = await Promise.all([
        chemicalApi.getAll(),
        labApi.getAll(),
        userApi.getAll(),
        purchaseApi.getAll(),
        usageApi.getAll(),
        taskApi.getAllScrapTasks(),
        wasteApi.getAllRecoveryTasks(),
      ]);

      setAllUsers(usersRes.data);
      if (usersRes.data.length > 0 && !currentUser) {
        setCurrentUser(usersRes.data[0]);
      }

      setStats({
        chemicals: chemicalsRes.data.length,
        labs: labsRes.data.length,
        users: usersRes.data.length,
        pendingPurchases: purchasesRes.data.filter(p => p.approvalStatus === 'PENDING').length,
        pendingUsages: usagesRes.data.filter(u => u.approvalStatus === 'PENDING').length,
        scrapTasks: scrapRes.data.filter(t => t.approvalStatus === 'PENDING').length,
        recoveryTasks: recoveryRes.data.filter(t => t.approvalStatus === 'PENDING').length,
      });
    } catch (error) {
      console.error('Failed to load stats:', error);
    }
  };

  const handleUserChange = (userId) => {
    const user = allUsers.find(u => u.id === userId);
    if (user) {
      setCurrentUser(user);
    }
  };

  return (
    <UserContext.Provider value={{ currentUser, allUsers }}>
      <Router>
        <div className="App">
          <nav className="navbar">
            <h1>实验室危化品管理系统</h1>
            <div className="nav-links">
              <NavLink to="/" end>仪表盘</NavLink>
              <NavLink to="/chemicals">危化品</NavLink>
              <NavLink to="/labs">实验室</NavLink>
              <NavLink to="/users">人员</NavLink>
              <NavLink to="/purchases">采购</NavLink>
              <NavLink to="/stock">入库</NavLink>
              <NavLink to="/usages">领用</NavLink>
              <NavLink to="/waste">废弃物</NavLink>
              <NavLink to="/tasks">任务</NavLink>
              <NavLink to="/safety-checks">安全检查</NavLink>
              <NavLink to="/audit-logs">审计日志</NavLink>
            </div>
            <div className="user-selector" style={{ marginLeft: 'auto', marginRight: '1rem' }}>
              <span style={{ color: 'white', marginRight: '0.5rem' }}>当前用户:</span>
              <select
                value={currentUser?.id || ''}
                onChange={(e) => handleUserChange(e.target.value)}
                style={{
                  padding: '0.3rem 0.5rem',
                  borderRadius: '4px',
                  border: 'none',
                  backgroundColor: 'rgba(255, 255, 255, 0.2)',
                  color: 'white',
                  cursor: 'pointer'
                }}
              >
                {allUsers.map((u) => (
                  <option key={u.id} value={u.id} style={{ color: 'black' }}>
                    {u.name} ({u.employeeId})
                  </option>
                ))}
              </select>
            </div>
          </nav>
          
          <div className="container">
            <Routes>
              <Route path="/" element={<Dashboard stats={stats} />} />
              <Route path="/chemicals" element={<ChemicalList />} />
              <Route path="/labs" element={<LabList />} />
              <Route path="/users" element={<UserList />} />
              <Route path="/purchases" element={<PurchaseList />} />
              <Route path="/stock" element={<StockList />} />
              <Route path="/usages" element={<UsageList />} />
              <Route path="/waste" element={<WasteList />} />
              <Route path="/tasks" element={<TaskList />} />
              <Route path="/safety-checks" element={<SafetyCheckList />} />
              <Route path="/audit-logs" element={<AuditLogList />} />
            </Routes>
          </div>
        </div>
      </Router>
    </UserContext.Provider>
  );
}

export default App;
