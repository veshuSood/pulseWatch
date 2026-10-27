
import './App.css';
import { useState, useEffect, useCallback } from 'react';
import axios from 'axios';

import StatCard from './components/StatCard';
import MonitorList from './components/MonitorList';
import AuthForm from './components/AuthForm';

const API_BASE_URL = 'http://localhost:8080/api';

function getUserEmail(token) {
  try {
    const payload = JSON.parse(atob(token.split('.')[1]));
    return payload.email || '';
  } catch {
    return '';
  }
}

function App() {
  const [monitors, setMonitors] = useState([]);
  const [statuses, setStatuses] = useState([]);

  const [name, setName] = useState('');
  const [url, setUrl] = useState('');
  const [checkIntervalSeconds, setCheckIntervalSeconds] = useState(60);

  const [formError, setFormError] = useState('');
  const [dashboardError, setDashboardError] = useState('');
  const [isSubmitting, setIsSubmitting] = useState(false);
  const [deletingId, setDeletingId] = useState(null);
  const [deleteError, setDeleteError] = useState('');

  const [token, setToken] = useState(
    () => localStorage.getItem('pulsewatch_token')
  );
  

  // Reusable authenticated request configuration.
  const getAuthConfig = useCallback(() => ({
    headers: {
      Authorization: `Bearer ${token}`,
    },
  }), [token]);

  // Called after successful login.
  const handleLogin = (jwt) => {
    localStorage.setItem('pulsewatch_token', jwt);
    setToken(jwt);
  };

  // Log out and clear dashboard data from memory.
  const handleLogout = () => {
    localStorage.removeItem('pulsewatch_token');
    setToken(null);
    setMonitors([]);
    setStatuses([]);
    setFormError('');
    setDashboardError('');
  };

  // Fetch monitors and their latest statuses.
  const fetchDashboardData = useCallback(async () => {
    if (!token) return;

    try {
      setDashboardError('');

      const response = await axios.get(
        `${API_BASE_URL}/monitors`,
        getAuthConfig()
      );

      const monitorList = response.data;
      setMonitors(monitorList);

      const statusResponses = await Promise.all(
        monitorList.map((monitor) =>
          axios.get(
            `${API_BASE_URL}/monitors/${monitor.id}/status`,
            getAuthConfig()
          )
        )
      );

      setStatuses(
        statusResponses.map((statusResponse) => statusResponse.data)
      );
    } catch (error) {
      console.error('Error fetching dashboard data:', error);

      if (error.response?.status === 401) {
        handleLogout();
        return;
      }

      setDashboardError(
        'Could not refresh dashboard data. Please try again.'
      );
    }
  }, [token, getAuthConfig]);
  useEffect(() => {
  const currentUrl = new URL(window.location.href);

  if (currentUrl.pathname === '/oauth-success') {
    const jwt = currentUrl.searchParams.get('token');

    // Remove the token from the visible URL immediately.
    window.history.replaceState({}, document.title, '/');

    if (jwt) {
      handleLogin(jwt);
    }
  }
}, []);

  // Initial load and automatic refresh every 10 seconds.
  useEffect(() => {
    if (!token) return;

    fetchDashboardData();

    const intervalId = setInterval(() => {
      fetchDashboardData();
    }, 10000);

    return () => clearInterval(intervalId);
  }, [token, fetchDashboardData]);

  // Add a monitor.
  const handleAddMonitor = async (event) => {
    event.preventDefault();

    setFormError('');
    setIsSubmitting(true);

    try {
      await axios.post(
        `${API_BASE_URL}/monitors`,
        {
          name: name.trim(),
          url: url.trim(),
          checkIntervalSeconds: Number(checkIntervalSeconds),
        },
        getAuthConfig()
      );

      // Clear the form after successful creation.
      setName('');
      setUrl('');
      setCheckIntervalSeconds(60);

      // Update the dashboard immediately.
      await fetchDashboardData();
    } catch (error) {
      console.error('Error adding monitor:', error);

      if (error.response?.status === 401) {
        handleLogout();
        return;
      }

      setFormError(
        error.response?.data?.message || 'Could not add monitor.'
      );
    } finally {
      setIsSubmitting(false);
    }
  };
  
const handleDeleteMonitor = async (monitor) => {
  const confirmed = window.confirm(
    `Delete "${monitor.name}"? Its monitoring history and alerts may also be deleted.`
  );

  if (!confirmed) return;

  setDeletingId(monitor.id);
  setDeleteError('');

  try {
    await axios.delete(
      `${API_BASE_URL}/monitors/${monitor.id}`,
      getAuthConfig()
    );

    await fetchDashboardData();
  } catch (error) {
    console.error('Error deleting monitor:', error);

    if (error.response?.status === 401) {
      handleLogout();
      return;
    }

    setDeleteError(
      error.response?.data?.message || 'Could not delete monitor.'
    );
  } finally {
    setDeletingId(null);
  }
};


  const monitorsUp = statuses.filter(
    (status) => status.currentStatus === 'UP'
  ).length;

  const monitorsDown = statuses.filter(
    (status) => status.currentStatus === 'DOWN'
  ).length;

  // Show authentication before the dashboard.
  if (!token) {
    return <AuthForm onLogin={handleLogin} />;
  }

  return (
    <div className="app">
      <header className="header">
  <div>
    <h1>PulseWatch</h1>
    <p>Website &amp; API Health Monitoring</p>
  </div>

  <div className="header-user">
    <span>{getUserEmail(token)}</span>
    <button
      type="button"
      className="logout-button"
      onClick={handleLogout}
    >
      Log out
    </button>
  </div>
</header>

      <section className="monitor-form">
        <h2>Add a Monitor</h2>

        <form onSubmit={handleAddMonitor}>
          <label>
            Monitor Name
            <input
              type="text"
              value={name}
              onChange={(event) => setName(event.target.value)}
              maxLength={120}
              required
            />
          </label>

          <label>
            Website URL
            <input
              type="url"
              value={url}
              onChange={(event) => setUrl(event.target.value)}
              placeholder="https://example.com"
              required
            />
          </label>

          <label>
            Check Interval (seconds)
            <input
              type="number"
              min={30}
              value={checkIntervalSeconds}
              onChange={(event) =>
                setCheckIntervalSeconds(event.target.value)
              }
              required
            />
          </label>

          {formError && <p role="alert">{formError}</p>}

          <button type="submit" disabled={isSubmitting}>
            {isSubmitting ? 'Adding...' : 'Add Monitor'}
          </button>
        </form>
      </section>

      <main>
        <h2>Dashboard Overview</h2>

        {dashboardError && (
          <p role="alert">{dashboardError}</p>
        )}

        <div className="stats-container">
          <StatCard
            title="Total Monitors"
            value={monitors.length}
            color="#3b82f6"
          />

          <StatCard
            title="Monitors UP"
            value={monitorsUp}
            color="#16a34a"
          />

          <StatCard
            title="Monitors DOWN"
            value={monitorsDown}
            color="#dc2626"
          />
        </div>

        {deleteError && <p role="alert">{deleteError}</p>}

<MonitorList
  monitors={monitors}
  statuses={statuses}
  onDelete={handleDeleteMonitor}
  deletingId={deletingId}
/>
      </main>
    </div>
  );
}

export default App;
