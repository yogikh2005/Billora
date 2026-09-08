import { useEffect, useState } from "react";
import {
    useNavigate,
    useParams
} from "react-router-dom";

import api from "../api/axios";
import "../css/users.css";


function UserForm() {

    const navigate = useNavigate();

    const { id } = useParams();

    const isEditMode = Boolean(id);


    // =========================
    // Form Data
    // =========================

    const [formData, setFormData] = useState({
        username: "",
        email: "",
        password: "",
        active: true,
        businessIds: []
    });


    // =========================
    // Businesses
    // =========================

    const [businesses, setBusinesses] = useState([]);

    const [loadingBusinesses, setLoadingBusinesses] =
        useState(true);


    // =========================
    // States
    // =========================

    const [loading, setLoading] = useState(false);

    const [saving, setSaving] = useState(false);

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

        const popupType = popup.type;

        setPopup({
            show: false,
            type: "success",
            message: ""
        });

        if (popupType === "success") {
            navigate("/users");
        }

    };


    // =========================
    // Load Businesses
    // =========================

    useEffect(() => {

        const fetchBusinesses = async () => {

            try {

                setLoadingBusinesses(true);

                const res = await api.get(
                    "/business"
                );

                console.log(
                    "Owner Businesses:",
                    res.data
                );

                setBusinesses(
                    Array.isArray(res.data)
                        ? res.data
                        : []
                );

            } catch (error) {

                console.error(
                    "Failed to load businesses:",
                    error
                );

                setError(
                    error.response?.data?.message ||
                    "Failed to load businesses"
                );

            } finally {

                setLoadingBusinesses(false);

            }

        };

        fetchBusinesses();

    }, []);


    // =========================
    // Load User
    // =========================

    useEffect(() => {

        if (!isEditMode) {

            setLoading(false);

            return;
        }


        const fetchUser = async () => {

            try {

                setLoading(true);

                setError("");


                const res = await api.get(
                    `/owner/users/${id}`
                );


                console.log(
                    "User:",
                    res.data
                );


                setFormData({

                    username:
                        res.data.username || "",

                    email:
                        res.data.email || "",

                    password: "",

                    active:
                        res.data.active ?? true,

                    businessIds:
                        res.data.businessIds || []

                });


            } catch (error) {

                console.error(error);


                const message =
                    error.response?.data?.message ||
                    error.response?.data ||
                    "Failed to load user";


                setError(message);

            } finally {

                setLoading(false);

            }

        };


        fetchUser();

    }, [id, isEditMode]);


    // =========================
    // Input Change
    // =========================

    const handleChange = (e) => {

        const {
            name,
            value,
            type,
            checked
        } = e.target;


        setFormData({

            ...formData,

            [name]:
                type === "checkbox"
                    ? checked
                    : value

        });

    };


    // =========================
    // Business Checkbox
    // =========================

    const handleBusinessChange = (businessId) => {

        setFormData((previous) => {

            const currentIds =
                previous.businessIds || [];


            const exists =
                currentIds.includes(businessId);


            return {

                ...previous,

                businessIds: exists

                    ? currentIds.filter(
                        id => id !== businessId
                    )

                    : [
                        ...currentIds,
                        businessId
                    ]

            };

        });

    };


    // =========================
    // Submit
    // =========================

    const handleSubmit = async (e) => {

        e.preventDefault();


        try {

            setSaving(true);

            setError("");


            // =========================
            // UPDATE USER
            // =========================

            if (isEditMode) {


                const updateData = {

                    username:
                        formData.username,

                    email:
                        formData.email,

                    active:
                        formData.active

                };


                // Password only if entered

                if (
                    formData.password.trim() !== ""
                ) {

                    updateData.password =
                        formData.password;

                }


                // --------------------------------
                // Update User Information
                // --------------------------------

                await api.put(

                    `/owner/users/${id}`,

                    updateData

                );


                // --------------------------------
                // Update Business Assignments
                // --------------------------------

                await api.put(

                    `/owner/users/${id}/businesses`,

                    {
                        businessIds:
                            formData.businessIds
                    }

                );


                showPopup(
                    formData.active
                        ? "Worker activated successfully"
                        : "Worker deactivated successfully"
                );

            }


            // =========================
            // CREATE USER
            // =========================

            else {


                const createData = {

                    username:
                        formData.username,

                    email:
                        formData.email,

                    password:
                        formData.password,

                    // IMPORTANT
                    // Send active status during creation

                    active:
                        formData.active,

                    businessIds:
                        formData.businessIds

                };


                await api.post(

                    "/owner/users",

                    createData

                );


                showPopup(
                    "Worker created successfully"
                );

            }


        } catch (error) {

            console.error(error);

            console.log(
                error.response?.data
            );


            const message =
                error.response?.data?.message ||
                error.response?.data ||
                "Something went wrong";


            showPopup(
                message,
                "error"
            );

        } finally {

            setSaving(false);

        }

    };


    // =========================
    // Loading
    // =========================

    if (
        loading ||
        loadingBusinesses
    ) {

        return (

            <div className="user-form-page">

                <div className="user-form-loading">

                    Loading...

                </div>

            </div>

        );

    }


    // =========================
    // Page
    // =========================

    return (

        <div className="user-form-page">


            {/* =========================
                Header
            ========================= */}

            <div className="user-form-header">

                <div>

                    <h1>

                        {isEditMode

                            ? "Update Worker"

                            : "Create Worker"

                        }

                    </h1>


                    <p>

                        {isEditMode

                            ? "Update worker information and business access"

                            : "Create a worker and assign business access"

                        }

                    </p>

                </div>

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
                Form
            ========================= */}

            <div className="user-form-card">


                <form
                    onSubmit={handleSubmit}
                >


                    {/* =========================
                        Username
                    ========================= */}

                    <div className="form-group">

                        <label>
                            Username
                        </label>


                        <input

                            type="text"

                            name="username"

                            value={
                                formData.username
                            }

                            onChange={
                                handleChange
                            }

                            placeholder="Enter username"

                            required

                        />

                    </div>


                    {/* =========================
                        Email
                    ========================= */}

                    <div className="form-group">

                        <label>
                            Email
                        </label>


                        <input

                            type="email"

                            name="email"

                            value={
                                formData.email
                            }

                            onChange={
                                handleChange
                            }

                            placeholder="Enter email"

                            required

                        />

                    </div>


                    {/* =========================
                        Password
                    ========================= */}

                    <div className="form-group">

                        <label>

                            Password

                            {isEditMode && (

                                <span className="optional">

                                    Optional

                                </span>

                            )}

                        </label>


                        <input

                            type="password"

                            name="password"

                            value={
                                formData.password
                            }

                            onChange={
                                handleChange
                            }

                            placeholder={

                                isEditMode

                                    ? "Leave blank to keep current password"

                                    : "Enter password"

                            }

                            required={
                                !isEditMode
                            }

                        />

                    </div>


                    {/* =========================
                        Active Worker
                    ========================= */}

                    <div className="form-checkbox">

                        <input

                            type="checkbox"

                            id="active"

                            name="active"

                            checked={
                                formData.active
                            }

                            onChange={
                                handleChange
                            }

                        />


                        <label htmlFor="active">

                            Active Worker

                        </label>


                        <span
                            className={
                                formData.active
                                    ? "active-status"
                                    : "inactive-status"
                            }
                        >

                            {formData.active
                                ? "Active"
                                : "Inactive"
                            }

                        </span>

                    </div>


                    {/* =========================
                        Business Assignment
                    ========================= */}

                    <div className="business-assignment">

                        <label className="business-title">

                            Assign Businesses

                        </label>


                        <p className="business-help">

                            Select the businesses this worker
                            is allowed to access.

                        </p>


                        {businesses.length === 0 ? (

                            <div className="no-businesses">

                                No businesses found.
                                Please create a business first.

                            </div>

                        ) : (

                            <div className="business-list">

                                {businesses.map(
                                    (business) => {

                                        const businessId =
                                            Number(
                                                business.id
                                            );


                                        const selected =
                                            formData.businessIds
                                                .includes(
                                                    businessId
                                                );


                                        return (

                                            <div

                                                className="business-item"

                                                key={
                                                    business.id
                                                }

                                            >

                                                <input

                                                    type="checkbox"

                                                    id={
                                                        `business-${business.id}`
                                                    }

                                                    checked={
                                                        selected
                                                    }

                                                    onChange={() =>
                                                        handleBusinessChange(
                                                            businessId
                                                        )
                                                    }

                                                />


                                                <label

                                                    htmlFor={
                                                        `business-${business.id}`
                                                    }

                                                >

                                                    <strong>

                                                        {
                                                            business.businessName
                                                        }

                                                    </strong>


                                                    {business.gstin && (

                                                        <span>

                                                            GSTIN:{" "}

                                                            {
                                                                business.gstin
                                                            }

                                                        </span>

                                                    )}

                                                </label>

                                            </div>

                                        );

                                    }

                                )}

                            </div>

                        )}

                    </div>


                    {/* =========================
                        Buttons
                    ========================= */}

                    <div className="form-actions">


                        <button

                            type="button"

                            className="cancel-button"

                            onClick={() =>
                                navigate("/users")
                            }

                            disabled={saving}

                        >

                            Cancel

                        </button>


                        <button

                            type="submit"

                            className="save-user-button"

                            disabled={
                                saving ||
                                loadingBusinesses
                            }

                        >

                            {saving

                                ? "Saving..."

                                : isEditMode

                                    ? "Update Worker"

                                    : "Create Worker"

                            }

                        </button>


                    </div>


                </form>

            </div>


            {/* =========================
                SUCCESS / ERROR POPUP
            ========================= */}

            {popup.show && (

                <div className="popup-overlay">


                    <div
                        className={`popup-box ${popup.type}`}
                    >


                        <div className="popup-icon">

                            {popup.type === "success"

                                ? "✓"

                                : "!"

                            }

                        </div>


                        <h3>

                            {popup.type === "success"

                                ? "Success"

                                : "Error"

                            }

                        </h3>


                        <p>

                            {popup.message}

                        </p>


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


export default UserForm;