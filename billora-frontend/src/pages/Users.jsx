import { useEffect, useState } from "react";
import { useNavigate } from "react-router-dom";

import api from "../api/axios";
import "../css/users.css";


function Users() {

    const navigate = useNavigate();


    // =========================
    // Users
    // =========================

    const [users, setUsers] = useState([]);

    const [loading, setLoading] = useState(true);

    const [error, setError] = useState("");


    // =========================
    // Popup
    // =========================

    const [popup, setPopup] = useState({
        show: false,
        type: "success",
        message: ""
    });


    // =========================
    // Show Popup
    // =========================

    const showPopup = (
        message,
        type = "success"
    ) => {

        setPopup({
            show: true,
            type,
            message
        });

    };


    // =========================
    // Close Popup
    // =========================

    const closePopup = () => {

        setPopup({
            show: false,
            type: "success",
            message: ""
        });

    };


    // =========================
    // Get All Users
    // =========================

    const fetchUsers = async () => {

        try {

            setLoading(true);

            setError("");


            const res = await api.get(
                "/owner/users"
            );


            console.log(
                "Users:",
                res.data
            );


            setUsers(res.data);


        } catch (error) {

            console.error(error);


            setError(
                error.response?.data?.message ||
                "Failed to load users"
            );

        } finally {

            setLoading(false);

        }

    };


    // =========================
    // Load Users
    // =========================

    useEffect(() => {

        fetchUsers();

    }, []);


    // =========================
    // Delete User
    // =========================

    const handleDelete = async (
        id,
        username
    ) => {

        const confirmDelete =
            window.confirm(
                `Are you sure you want to delete "${username}"?`
            );


        if (!confirmDelete) {
            return;
        }


        try {

            await api.delete(
                `/owner/users/${id}`
            );


            showPopup(
                "User deleted successfully"
            );


            fetchUsers();


        } catch (error) {

            console.error(error);


            showPopup(

                error.response?.data?.message ||
                "Failed to delete user",

                "error"

            );

        }

    };


    // =========================
    // Update User
    // =========================

    const handleUpdate = (id) => {

        navigate(
            `/users/edit/${id}`
        );

    };


    // =========================
    // Add User
    // =========================

    const handleAddUser = () => {

        navigate(
            "/users/add"
        );

    };


    // =========================
    // Loading
    // =========================

    if (loading) {

        return (

            <div className="users-page">

                <div className="users-loading">

                    Loading users...

                </div>

            </div>

        );

    }


    // =========================
    // Page
    // =========================

    return (

        <div className="users-page">


            {/* =========================
                Header
            ========================= */}

            <div className="users-header">

                <div>

                    <h1>
                        User Management
                    </h1>

                    <p>
                        Manage users and their access permissions
                    </p>

                </div>


                <button
                    className="add-user-button"
                    onClick={handleAddUser}
                >
                    + Add User
                </button>

            </div>


            {/* =========================
                Error
            ========================= */}

            {error && (

                <div className="users-error">

                    {error}

                </div>

            )}


            {/* =========================
                User Table
            ========================= */}

            <div className="users-table-card">


                {/* Table Header */}

                <div className="table-header">

                    <div>

                        <h2>
                            All Users
                        </h2>

                        <span>

                            {users.length} user

                            {users.length !== 1
                                ? "s"
                                : ""}

                        </span>

                    </div>

                </div>


                {/* =========================
                    Empty
                ========================= */}

                {users.length === 0 ? (

                    <div className="empty-users">

                        No users found.

                    </div>

                ) : (


                    /* =========================
                       Table
                    ========================= */

                    <div className="table-wrapper">

                        <table className="users-table">


                            {/* Header */}

                            <thead>

                                <tr>

                                    <th>
                                        ID
                                    </th>

                                    <th>
                                        Username
                                    </th>

                                    <th>
                                        Email
                                    </th>

                                    <th>
                                        Role
                                    </th>

                                    <th>
                                        Status
                                    </th>

                                    <th>
                                        Created At
                                    </th>

                                    <th>
                                        Updated At
                                    </th>
                                    <th>
                                        Allocated Businesses
                                    </th>
                                    <th>
                                        Action
                                    </th>

                                </tr>

                            </thead>


                            {/* Body */}

                            <tbody>

                                {users.map(
                                    (user) => (

                                        <tr
                                            key={
                                                user.id
                                            }
                                        >


                                            {/* ID */}

                                            <td>

                                                #{user.id}

                                            </td>


                                            {/* Username */}

                                            <td>

                                                <div className="user-name-cell">


                                                    <div className="user-avatar">

                                                        {user.username
                                                            ?.charAt(
                                                                0
                                                            )
                                                            .toUpperCase()}

                                                    </div>


                                                    <strong>

                                                        {
                                                            user.username
                                                        }

                                                    </strong>

                                                </div>

                                            </td>


                                            {/* Email */}

                                            <td>

                                                {
                                                    user.email
                                                }

                                            </td>


                                            {/* Role */}

                                            <td>

                                                <span

                                                    className={
                                                        user.role ===
                                                        "ROLE_OWNER"

                                                            ? "role-badge owner"

                                                            : "role-badge user"
                                                    }

                                                >

                                                    {
                                                        user.role ===
                                                        "ROLE_OWNER"

                                                            ? "OWNER"

                                                            : "USER"
                                                    }

                                                </span>

                                            </td>


                                            {/* Status */}

                                            <td>

                                                <span

                                                    className={
                                                        user.active

                                                            ? "status-badge active"

                                                            : "status-badge inactive"
                                                    }

                                                >

                                                    {
                                                        user.active

                                                            ? "Active"

                                                            : "Inactive"
                                                    }

                                                </span>

                                            </td>


                                            {/* Created */}

                                            <td>

                                                {
                                                    user.createdAt

                                                        ? new Date(
                                                            user.createdAt
                                                        ).toLocaleDateString()

                                                        : "-"
                                                }

                                            </td>


                                            {/* Updated */}

                                            <td>

                                                {
                                                    user.updatedAt

                                                        ? new Date(
                                                            user.updatedAt
                                                        ).toLocaleDateString()

                                                        : "-"
                                                }

                                            </td>

                                                        <td>
                                                            {user.businesses && user.businesses.length > 0 ? (
                                                                <div className="allocated-businesses">

                                                                    {user.businesses.map((business) => (
                                                                        <span
                                                                            key={business.id}
                                                                            className="business-badge"
                                                                        >
                                                                            {business.businessName}
                                                                        </span>
                                                                    ))}

                                                                </div>
                                                            ) : (
                                                                <span className="no-business">
                                                                    No business assigned
                                                                </span>
                                                            )}
                                                        </td>


                                            {/* Actions */}

                                            <td>

                                                <div className="action-buttons">


                                                    {/* Update */}

                                                    <button

                                                        className="update-button"

                                                        onClick={() =>
                                                            handleUpdate(
                                                                user.id
                                                            )
                                                        }

                                                    >

                                                        ✏️ Update

                                                    </button>


                                                    {/* Delete */}

                                                    <button

                                                        className="delete-button"

                                                        onClick={() =>
                                                            handleDelete(
                                                                user.id,
                                                                user.username
                                                            )
                                                        }

                                                    >

                                                        🗑️ Delete

                                                    </button>


                                                </div>

                                            </td>

                                        </tr>

                                    )
                                )}

                            </tbody>

                        </table>

                    </div>

                )}

            </div>


            {/* =========================
                SUCCESS / ERROR POPUP
            ========================= */}

            {popup.show && (

                <div className="popup-overlay">


                    <div
                        className={`popup-box ${popup.type}`}
                    >


                        {/* Icon */}

                        <div className="popup-icon">

                            {popup.type === "success"

                                ? "✓"

                                : "!"

                            }

                        </div>


                        {/* Title */}

                        <h3>

                            {popup.type === "success"

                                ? "Success"

                                : "Error"

                            }

                        </h3>


                        {/* Message */}

                        <p>

                            {popup.message}

                        </p>


                        {/* OK */}

                        <button

                            type="button"

                            className="popup-button"

                            onClick={
                                closePopup
                            }

                        >

                            OK

                        </button>


                    </div>

                </div>

            )}

        </div>

    );

}


export default Users;