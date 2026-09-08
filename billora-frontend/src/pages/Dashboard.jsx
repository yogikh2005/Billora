import { Link } from "react-router-dom";
import "../css/dashboard.css";
import api from "../api/axios";
import { useState, useEffect } from "react";

function Dashboard() {

    const user = JSON.parse(
        sessionStorage.getItem("user")
    );

    const isOwner = user?.role === "ROLE_OWNER";

    const [dashboardData, setDashboardData] = useState({
        stats: {
            totalUsers: 0,
            totalBusinesses: 0,
            totalInvoices: 0,
            totalRevenue: 0,
            totalGst: 0
        },
        recentInvoices: [],
        recentUsers: []
    });

    useEffect(() => {

        const loadDashboard = async () => {

            try {

                const response = await api.get("/dashboard");

                console.log("Dashboard:", response.data);

                setDashboardData(response.data);

            } catch (error) {

                console.error(
                    "Failed to load dashboard:",
                    error
                );

            }
        };

        loadDashboard();

    }, []);

    const stats = dashboardData.stats || {};

    return (
        <div>

            {/* Page Header */}
            <div className="page-header">

                <div>

                    <h2>Dashboard</h2>

                    <p>
                    Manage your GST billing and invoices with ease.
                    </p>

                </div>

            </div>


            {/* =========================
                Statistics Cards
            ========================= */}

            <div className="cards">

                {/* OWNER ONLY - Users */}

                {isOwner && (

                    <div className="card">

                        <div className="card-icon users-icon">
                            👥
                        </div>

                        <div>

                            <p>Total Users</p>

                            <h1>
                                {stats.totalUsers ?? 0}
                            </h1>

                        </div>

                        <Link
                            to="/users"
                            className="card-action"
                        >
                            View Users →
                        </Link>

                    </div>

                )}


                {/* OWNER ONLY - Business */}

                {isOwner && (

                    <div className="card">

                        <div className="card-icon business-icon">
                            🏢
                        </div>

                        <div>

                            <p>Total Businesses</p>

                            <h1>
                                {stats.totalBusinesses ?? 0}
                            </h1>

                        </div>

                        <Link
                            to="/business"
                            className="card-action"
                        >
                            View Business →
                        </Link>

                    </div>

                )}


                {/* ALL USERS - Invoice */}

                <div className="card">

                    <div className="card-icon invoice-icon">
                        🧾
                    </div>

                    <div>

                        <p>Total Invoices</p>

                        <h1>
                            {stats.totalInvoices ?? 0}
                        </h1>

                    </div>

                    <Link
                        to="/invoice"
                        className="card-action"
                    >
                        View Invoices →
                    </Link>

                </div>


                {/* ALL USERS - Revenue */}

                <div className="card">

                    <div className="card-icon revenue-icon">
                        ₹
                    </div>

                    <div>

                        <p>Total Revenue</p>

                        <h1>
                            ₹{Number(
                                stats.totalRevenue ?? 0
                            ).toLocaleString("en-IN")}
                        </h1>

                    </div>

                    <Link
                        to="/reports"
                        className="card-action"
                    >
                        View Reports →
                    </Link>

                </div>

            </div>


            {/* =========================
                Quick Actions
            ========================= */}

            <div className="section">

                <div className="section-header">

                    <h3>Quick Actions</h3>

                    <p>
                        Frequently used operations
                    </p>

                </div>


                <div className="actions">

                    {/* OWNER ONLY */}

                    {isOwner && (

                        <Link
                            to="/users/add"
                            className="action-button"
                        >

                            <span>👤</span>

                            <div>

                                <strong>
                                    Add User
                                </strong>

                                <small>
                                    Create a new user
                                </small>

                            </div>

                        </Link>

                    )}


                    {/* OWNER ONLY */}

                    {isOwner && (

                        <Link
                            to="/business/add"
                            className="action-button"
                        >

                            <span>🏢</span>

                            <div>

                                <strong>
                                    Add Business
                                </strong>

                                <small>
                                    Register a business
                                </small>

                            </div>

                        </Link>

                    )}


                    {/* ALL USERS */}

                    <Link
                        to="/invoice/create"
                        className="action-button"
                    >

                        <span>🧾</span>

                        <div>

                            <strong>
                                Create Invoice
                            </strong>

                            <small>
                                Create new GST invoice
                            </small>

                        </div>

                    </Link>


                    {/* ALL USERS */}

                    <Link
                        to="/reports"
                        className="action-button"
                    >

                        <span>📊</span>

                        <div>

                            <strong>
                                Generate Report
                            </strong>

                            <small>
                                View GST reports
                            </small>

                        </div>

                    </Link>

                </div>

            </div>


            {/* =========================
                Bottom Section
            ========================= */}

            <div className="dashboard-grid">


                {/* =========================
                    Recent Invoices
                ========================= */}

                <div className="dashboard-panel">

                    <div className="panel-header">

                        <div>

                            <h3>
                                Recent Invoices
                            </h3>

                            <p>
                                Latest generated invoices
                            </p>

                        </div>

                        <Link to="/invoice">
                            View All
                        </Link>

                    </div>


                    <table>

                        <thead>

                            <tr>

                                <th>
                                    Invoice
                                </th>

                                <th>
                                    Customer
                                </th>

                                <th>
                                    Amount
                                </th>

                                <th>
                                    Status
                                </th>

                            </tr>

                        </thead>


                        <tbody>

                            {dashboardData.recentInvoices?.length > 0 ? (

                                dashboardData.recentInvoices.map(
                                    (invoice) => (

                                        <tr key={invoice.id}>

                                            <td>
                                                #{invoice.invoiceNumber}
                                            </td>

                                            <td>
                                                {invoice.customerName || "-"}
                                            </td>

                                            <td>
                                                ₹{Number(
                                                    invoice.amount || 0
                                                ).toLocaleString("en-IN")}
                                            </td>

                                            <td>

                                                <span
                                                    className={
                                                        `status ${
                                                            invoice.status === "PAID"
                                                                ? "success"
                                                                : "pending"
                                                        }`
                                                    }
                                                >
                                                    {invoice.status}
                                                </span>

                                            </td>

                                        </tr>

                                    )
                                )

                            ) : (

                                <tr>

                                    <td
                                        colSpan="4"
                                        style={{
                                            textAlign: "center"
                                        }}
                                    >
                                        No invoices found
                                    </td>

                                </tr>

                            )}

                        </tbody>

                    </table>

                </div>


                {/* =========================
                    Recent Users
                    OWNER ONLY
                ========================= */}

                {isOwner && (

                    <div className="dashboard-panel">

                        <div className="panel-header">

                            <div>

                                <h3>
                                    Recent Users
                                </h3>

                                <p>
                                    Recently registered users
                                </p>

                            </div>

                            <Link to="/users">
                                View All
                            </Link>

                        </div>


                        <div className="user-list">

                            {dashboardData.recentUsers?.length > 0 ? (

                                dashboardData.recentUsers.map(
                                    (item) => (

                                        <div
                                            className="user-item"
                                            key={item.id}
                                        >

                                            <div className="avatar">

                                                {item.name
                                                    ?.substring(0, 2)
                                                    .toUpperCase()}

                                            </div>

                                            <div>

                                                <strong>
                                                    {item.name}
                                                </strong>

                                                <p>
                                                    {item.email}
                                                </p>

                                            </div>

                                        </div>

                                    )
                                )

                            ) : (

                                <p>
                                    No users found
                                </p>

                            )}

                        </div>

                    </div>

                )}

            </div>

        </div>
    );
}

export default Dashboard;