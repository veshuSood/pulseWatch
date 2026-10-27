
import StatusBadge from './StatusBadge';
import { formatDateTime } from '../utils/formatDateTime';
import './MonitorList.css';

function MonitorList({ monitors, statuses, onDelete, deletingId }) {
  return (
    <section className="monitor-section">
      <h2>Your Monitors</h2>

      <div className="monitor-table-container">
        <table className="monitor-table">
          <thead>
            <tr>
              <th>Website</th>
              <th>Status</th>
              <th>Response Time</th>
              <th>HTTP Code</th>
              <th>Last Checked (IST)</th>
              <th>Action</th>
            </tr>
          </thead>

          <tbody>
            {monitors.map((monitor) => {
              const status = statuses.find(
                (item) => item.monitorId === monitor.id
              );

              const isDeleting = deletingId === monitor.id;

              return (
                <tr key={monitor.id}>
                  <td>
                    <strong>{monitor.name}</strong>
                    <br />
                    <small>{monitor.url}</small>
                  </td>

                  <td>
                    <StatusBadge status={status?.currentStatus} />
                  </td>

                  <td>
                    {status?.responseTimeMs != null
                      ? `${status.responseTimeMs} ms`
                      : '—'}
                  </td>

                  <td>{status?.statusCode ?? '—'}</td>

                  <td>{formatDateTime(status?.lastCheckedAt)}</td>

                  <td>
                    <button
                      type="button"
                      className="delete-monitor-button"
                      disabled={isDeleting}
                      onClick={() => onDelete(monitor)}
                    >
                      {isDeleting ? 'Deleting...' : 'Delete'}
                    </button>
                  </td>
                </tr>
              );
            })}

            {monitors.length === 0 && (
              <tr>
                <td colSpan={6}>No monitors yet. Add one above.</td>
              </tr>
            )}
          </tbody>
        </table>
      </div>
    </section>
  );
}

export default MonitorList;
