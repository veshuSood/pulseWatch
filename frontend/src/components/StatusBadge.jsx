function StatusBadge({ status }) {
  const normalizedStatus = status?.toUpperCase() ?? "UNKNOWN";

  let badgeClass = "status-unknown";

  if (normalizedStatus === "UP") {
    badgeClass = "status-up";
  } else if (normalizedStatus === "DOWN") {
    badgeClass = "status-down";
  }

  return (
    <span className={`status-badge ${badgeClass}`}>
      <span className="status-dot"></span>
      {normalizedStatus}
    </span>
  );
}

export default StatusBadge;