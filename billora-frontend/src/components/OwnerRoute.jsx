import { Navigate, Outlet } from "react-router-dom";

function OwnerRoute() {
    const token = sessionStorage.getItem("token");

    const user = JSON.parse(
        sessionStorage.getItem("user")
    );

    // Not logged in
    if (!token) {
        return <Navigate to="/" replace />;
    }

    // Logged in but not OWNER
    if (user?.role !== "ROLE_OWNER") {
        return <Navigate to="/dashboard" replace />;
    }

    return <Outlet />;
}

export default OwnerRoute;