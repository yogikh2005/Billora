import { NavLink, useNavigate } from "react-router-dom";

function Sidebar() {
  const navigate = useNavigate();

    const user = JSON.parse(
      sessionStorage.getItem("user") || "null"
  );

  console.log("SESSION USER =", user);
  console.log("ROLE =", user?.role);

  const isOwner =
      user?.role?.toUpperCase() === "ROLE_OWNER";

  console.log("IS OWNER =", isOwner);

  const handleLogout = () => {
  sessionStorage.removeItem("token");
  sessionStorage.removeItem("user");

    navigate("/login", { replace: true });
  };

  return (
    <aside className="sidebar">

      {/* Logo */}
      <div className="logo">
        <div className="logo-icon">
          GST
        </div>

        <div className="logo-text">
          <h2>Billora</h2>

        </div>
      </div>


      {/* Main Menu */}
      <div className="menu-title">
        MAIN MENU
      </div>

      <nav>

        <NavLink to="/dashboard">
          <span>📊</span>
          Dashboard
        </NavLink>


        {/* OWNER ONLY */}
        {isOwner && (
          <NavLink to="/users">
            <span>👥</span>
            Users
          </NavLink>
        )}


        {/* OWNER ONLY */}
        {isOwner && (
          <NavLink to="/business">
            <span>🏢</span>
            Business
          </NavLink>
        )}


        <NavLink to="/invoice">
          <span>🧾</span>
          Invoices
        </NavLink>


        <NavLink to="/reports">
          <span>📈</span>
          Reports
        </NavLink>


        {/* OWNER ONLY */}
        {isOwner && (
          <NavLink to="/logs">
            <span>📋</span>
            Activity Logs
          </NavLink>
        )}

        {/* OWNER ONLY */}
        {isOwner && (
          <NavLink to="/profile">
            <span>👤</span>
            Profile
          </NavLink>
        )}

      </nav>


      {/* Actions */}
      <div className="menu-title">
        ACTIONS
      </div>

      <nav>

        {/* OWNER ONLY */}
        {isOwner && (
          <NavLink to="/users/add">
            <span>➕</span>
            Add User
          </NavLink>
        )}


        {/* OWNER ONLY */}
        {isOwner && (
          <NavLink to="/business/add">
            <span>➕</span>
            Add Business
          </NavLink>
        )}


        <NavLink to="/invoice/create">
          <span>➕</span>
          Create Invoice
        </NavLink>

      </nav>


      {/* Logout */}
      <div className="sidebar-bottom">

        <button
          className="logout-button"
          onClick={handleLogout}
        >
          🚪 Logout
        </button>

      </div>

    </aside>
  );
}

export default Sidebar;