import { useEffect, useState } from "react";
import api from "../api/axios";
import "../css/AuditLog.css";

function AuditLog() {

    const [logs, setLogs] = useState([]);

    const [page, setPage] = useState(0);
    const [size] = useState(10);

    const [totalPages, setTotalPages] = useState(0);

    const [loading, setLoading] = useState(true);
    const [error, setError] = useState("");


    const loadLogs = async () => {

        try {

            setLoading(true);
            setError("");

            const response = await api.get(
                `/audit?page=${page}&size=${size}`
            );

            console.log(
                "Audit Logs:",
                response.data
            );

            setLogs(
                response.data.content || []
            );

            setTotalPages(
                response.data.totalPages || 0
            );

        } catch (error) {

            console.error(
                "Failed to load audit logs:",
                error
            );

            setError(
                "Failed to load audit logs."
            );

        } finally {

            setLoading(false);
        }
    };


    useEffect(() => {

        loadLogs();

    }, [page]);


    const formatDate = (timestamp) => {

        if (!timestamp) {
            return "-";
        }

        return new Date(timestamp).toLocaleString(
            "en-IN",
            {
                day: "2-digit",
                month: "short",
                year: "numeric",
                hour: "2-digit",
                minute: "2-digit"
            }
        );
    };


    const getActionClass = (action) => {

        if (!action) {
            return "action-default";
        }

        const value =
            action.toLowerCase();

        if (value.includes("create")) {
            return "action-create";
        }

        if (value.includes("update")) {
            return "action-update";
        }

        if (
            value.includes("delete")
            || value.includes("remove")
        ) {
            return "action-delete";
        }

        if (
            value.includes("login")
            || value.includes("active")
        ) {
            return "action-login";
        }

        return "action-default";
    };


    const handlePrevious = () => {

        if (page > 0) {
            setPage(page - 1);
        }
    };


    const handleNext = () => {

        if (page < totalPages - 1) {
            setPage(page + 1);
        }
    };


    return (

        <div className="audit-page">

            {/* =========================
                HEADER
            ========================= */}

            <div className="audit-header">

                <div>

                    <h1>
                        GST Reports
                    </h1>

                    <p>
                        Track system activities and
                        user actions
                    </p>

                </div>


                <button
                    className="audit-refresh-button"
                    onClick={loadLogs}
                    disabled={loading}
                >
                    ↻ Refresh
                </button>

            </div>


            {/* =========================
                REPORT CARD
            ========================= */}

            <div className="audit-card">

                <div className="audit-card-header">

                    <div>

                        <h2>
                            Audit Logs
                        </h2>

                        <span>
                            System activity history
                        </span>

                    </div>


                    <div className="audit-count">

                        {logs.length} Records

                    </div>

                </div>


                {/* =========================
                    ERROR
                ========================= */}

                {error && (

                    <div className="audit-error">

                        <span>
                            ⚠
                        </span>

                        {error}

                    </div>

                )}


                {/* =========================
                    LOADING
                ========================= */}

                {loading ? (

                    <div className="audit-loading">

                        <div className="audit-spinner"></div>

                        <p>
                            Loading audit logs...
                        </p>

                    </div>

                ) : logs.length === 0 ? (

                    /* =========================
                       EMPTY
                    ========================= */

                    <div className="audit-empty">

                        <div className="audit-empty-icon">
                            ✓
                        </div>

                        <h3>
                            No Audit Records
                        </h3>

                        <p>
                            There are no system activities
                            to display yet.
                        </p>

                    </div>

                ) : (

                    /* =========================
                       TABLE
                    ========================= */

                    <div className="audit-table-container">

                        <table className="audit-table">

                            <thead>

                                <tr>

                                    <th>
                                        ID
                                    </th>

                                    <th>
                                        Action
                                    </th>

                                    <th>
                                        Performed By
                                    </th>

                                    <th>
                                        Date & Time
                                    </th>

                                    <th>
                                        Details
                                    </th>

                                </tr>

                            </thead>


                            <tbody>

                                {logs.map((log) => (

                                    <tr key={log.id}>

                                        <td>

                                            <span className="audit-id">
                                                #{log.id}
                                            </span>

                                        </td>


                                        <td>

                                            <span
                                                className={`action-badge ${getActionClass(
                                                    log.action
                                                )}`}
                                            >
                                                {log.action}
                                            </span>

                                        </td>


                                        <td>

                                            <div className="user-cell">

                                                <div className="user-avatar">

                                                    {log.performedBy
                                                        ? log.performedBy
                                                            .charAt(0)
                                                            .toUpperCase()
                                                        : "?"}

                                                </div>

                                                <span>
                                                    {log.performedBy || "-"}
                                                </span>

                                            </div>

                                        </td>


                                        <td>

                                            <span className="audit-date">

                                                {formatDate(
                                                    log.timestamp
                                                )}

                                            </span>

                                        </td>


                                        <td>

                                            <span className="audit-details">

                                                {log.details || "-"}

                                            </span>

                                        </td>

                                    </tr>

                                ))}

                            </tbody>

                        </table>

                    </div>

                )}


                {/* =========================
                    PAGINATION
                ========================= */}

                {!loading &&
                    logs.length > 0 && (

                    <div className="audit-pagination">

                        <span className="pagination-info">

                            Page {page + 1}
                            {" "}of{" "}
                            {totalPages || 1}

                        </span>


                        <div className="pagination-buttons">

                            <button
                                onClick={
                                    handlePrevious
                                }
                                disabled={
                                    page === 0
                                }
                            >
                                ← Previous
                            </button>


                            <button
                                onClick={
                                    handleNext
                                }
                                disabled={
                                    page >=
                                    totalPages - 1
                                }
                            >
                                Next →
                            </button>

                        </div>

                    </div>

                )}

            </div>

        </div>
    );
}

export default AuditLog;