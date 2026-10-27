function StatCard({ title, value, color }) {
  return (
    <div className="stat-card">
      <h3>{title}</h3>
      <p style={{color}}>{value}</p>
    </div>
  );
}

export default StatCard;