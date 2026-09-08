import { useEffect, useState } from "react";
import {
    useNavigate,
    useParams
} from "react-router-dom";

import api from "../api/axios";
import "../css/business.css";


function BusinessForm() {

    const navigate = useNavigate();

    const { id } = useParams();

    const isEditMode = Boolean(id);


    // =========================
    // Form Data
    // =========================

    const [formData, setFormData] = useState({

        businessName: "",
        address: "",
        city: "",
        state: "",
        pincode: "",
        gstin: "",
        email: "",
        mobileNo: "",
        phoneNo: "",
        website: "",
        logoPath: "",
        bankName: "",
        accountNumber: "",
        ifscCode: "",
        branch: "",
        termsAndConditions: "",
        active: true

    });


    // =========================
    // Logo
    // =========================

    const [logo, setLogo] = useState(null);


    // =========================
    // Loading / Saving
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

        setPopup({

            show: false,

            type: "success",

            message: ""

        });

        navigate("/business");

    };


    // =========================
    // Load Business
    // Only Update
    // =========================

    useEffect(() => {

        if (!isEditMode) {
            return;
        }


        const fetchBusiness = async () => {

            try {

                setLoading(true);

                setError("");


                const res = await api.get(
                    `/business/${id}`
                );


                console.log(
                    "Business:",
                    res.data
                );


                setFormData({

                    businessName:
                        res.data.businessName || "",

                    address:
                        res.data.address || "",

                    city:
                        res.data.city || "",

                    state:
                        res.data.state || "",

                    pincode:
                        res.data.pincode || "",

                    gstin:
                        res.data.gstin || "",

                    email:
                        res.data.email || "",

                    mobileNo:
                        res.data.mobileNo || "",

                    phoneNo:
                        res.data.phoneNo || "",

                    website:
                        res.data.website || "",

                    logoPath:
                        res.data.logoPath || "",

                    bankName:
                        res.data.bankName || "",

                    accountNumber:
                        res.data.accountNumber || "",

                    ifscCode:
                        res.data.ifscCode || "",

                    branch:
                        res.data.branch || "",

                    termsAndConditions:
                        res.data.termsAndConditions || "",

                    active:
                        res.data.active ?? true

                });


            } catch (error) {

                console.error(error);


                const message =
                    error.response?.data?.message ||
                    "Failed to load business";


                setError(message);

            } finally {

                setLoading(false);

            }

        };


        fetchBusiness();

    }, [id, isEditMode]);


    // =========================
    // Handle Input
    // =========================

    const handleChange = (e) => {

        setFormData({

            ...formData,

            [e.target.name]:
                e.target.value

        });

    };


    // =========================
    // Handle Logo
    // =========================

    const handleLogoChange = (e) => {

        const file = e.target.files[0];

        if (!file) {
            return;
        }


        // =================================
        // Validate File Type
        // =================================

        const allowedTypes = [
            "image/png",
            "image/jpeg",
            "image/jpg"
        ];


        if (!allowedTypes.includes(file.type)) {

            showPopup(
                "Only PNG, JPG and JPEG images are allowed.",
                "error"
            );

            e.target.value = "";

            return;
        }


        // =================================
        // Validate File Size
        // 2 MB
        // =================================

        if (file.size > 2 * 1024 * 1024) {

            showPopup(
                "Logo size must be less than 2 MB.",
                "error"
            );

            e.target.value = "";

            return;
        }


        setLogo(file);

    };


    // =========================
    // Submit
    // =========================

    const handleSubmit = async (e) => {

        e.preventDefault();


        try {

            setSaving(true);

            setError("");


            // =========================================
            // UPDATE BUSINESS
            // =========================================

            if (isEditMode) {

                await api.put(

                    `/business/${id}`,

                    formData

                );


                showPopup(
                    "Business updated successfully"
                );

            }


            // =========================================
            // CREATE BUSINESS
            // =========================================

            else {

                const data =
                    new FormData();


                // =====================================
                // Business DTO
                // =====================================

                const businessData = {

                    ...formData,

                    logoPath: null,

                    active: true

                };


                data.append(

                    "business",

                    new Blob(

                        [
                            JSON.stringify(
                                businessData
                            )
                        ],

                        {
                            type:
                                "application/json"
                        }

                    )

                );


                // =====================================
                // Logo
                // =====================================

                if (logo) {

                    data.append(
                        "logo",
                        logo
                    );

                }


                // =====================================
                // API CALL
                // =====================================

                await api.post(

                    "/business",

                    data

                );


                showPopup(
                    "Business created successfully"
                );

            }


        } catch (error) {

            console.error(
                "Business save error:",
                error
            );


            console.log(
                "Status:",
                error.response?.status
            );


            console.log(
                "Response:",
                error.response?.data
            );


            const message =
                error.response?.data?.message ||
                "Failed to save business";


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

    if (loading) {

        return (

            <div className="business-form-page">

                <div className="business-loading">

                    Loading business...

                </div>

            </div>

        );

    }


    // =========================
    // Page
    // =========================

    return (

        <div className="business-form-page">


            {/* =================================
                HEADER
            ================================= */}

            <div className="business-form-header">

                <div>

                    <h1>

                        {isEditMode

                            ? "Update Business"

                            : "Create Business"

                        }

                    </h1>


                    <p>

                        {isEditMode

                            ? "Update business information"

                            : "Register a new business"

                        }

                    </p>

                </div>

            </div>


            {/* =================================
                ERROR
            ================================= */}

            {error && (

                <div className="business-error">

                    {error}

                </div>

            )}


            {/* =================================
                FORM CARD
            ================================= */}

            <div className="business-form-card">

                <form
                    onSubmit={handleSubmit}
                >


                    {/* =================================
                        BUSINESS INFORMATION
                    ================================= */}

                    <div className="form-section">

                        <h3>
                            Business Information
                        </h3>


                        <div className="form-grid">


                            {/* Business Name */}

                            <div className="form-group full">

                                <label>
                                    Business Name
                                </label>


                                <input
                                    type="text"
                                    name="businessName"
                                    value={
                                        formData.businessName
                                    }
                                    onChange={
                                        handleChange
                                    }
                                    placeholder="Enter business name"
                                    required
                                />

                            </div>


                            {/* Address */}

                            <div className="form-group full">

                                <label>
                                    Address
                                </label>


                                <textarea
                                    name="address"
                                    value={
                                        formData.address
                                    }
                                    onChange={
                                        handleChange
                                    }
                                    placeholder="Enter business address"
                                    rows="3"
                                    required
                                />

                            </div>


                            {/* City */}

                            <div className="form-group">

                                <label>
                                    City
                                </label>


                                <input
                                    type="text"
                                    name="city"
                                    value={
                                        formData.city
                                    }
                                    onChange={
                                        handleChange
                                    }
                                    placeholder="City"
                                    required
                                />

                            </div>


                            {/* State */}

                            <div className="form-group">

                                <label>
                                    State
                                </label>


                                <input
                                    type="text"
                                    name="state"
                                    value={
                                        formData.state
                                    }
                                    onChange={
                                        handleChange
                                    }
                                    placeholder="State"
                                    required
                                />

                            </div>


                            {/* Pincode */}

                            <div className="form-group">

                                <label>
                                    Pincode
                                </label>


                                <input
                                    type="text"
                                    name="pincode"
                                    value={
                                        formData.pincode
                                    }
                                    onChange={
                                        handleChange
                                    }
                                    placeholder="Pincode"
                                />

                            </div>


                            {/* GSTIN */}

                            <div className="form-group">

                                <label>
                                    GSTIN
                                </label>


                                <input
                                    type="text"
                                    name="gstin"
                                    value={
                                        formData.gstin
                                    }
                                    onChange={
                                        handleChange
                                    }
                                    placeholder="GSTIN"
                                />

                            </div>


                            {/* =================================
                                BUSINESS LOGO
                            ================================= */}

                            <div className="form-group full">

                                <label>
                                    Business Logo
                                </label>


                                <input
                                    type="file"
                                    accept=".png,.jpg,.jpeg,image/png,image/jpeg"
                                    onChange={
                                        handleLogoChange
                                    }
                                />


                                <small className="file-help">

                                    Allowed formats:
                                    PNG, JPG, JPEG.
                                    Maximum size: 2 MB.

                                </small>


                                {/* Selected File */}

                                {logo && (

                                    <div className="selected-logo">

                                        Selected:

                                        {" "}

                                        <strong>
                                            {logo.name}
                                        </strong>

                                    </div>

                                )}


                                {/* Existing Logo */}

                                {isEditMode &&
                                    formData.logoPath && (
                                        <div className="existing-logo">

                                            Current logo:

                                            {" "}

                                            <strong>
                                                Available
                                            </strong>

                                        </div>
                                    )}

                            </div>


                        </div>

                    </div>


                    {/* =================================
                        CONTACT INFORMATION
                    ================================= */}

                    <div className="form-section">

                        <h3>
                            Contact Information
                        </h3>


                        <div className="form-grid">


                            {/* Email */}

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
                                    placeholder="business@example.com"
                                />

                            </div>


                            {/* Mobile */}

                            <div className="form-group">

                                <label>
                                    Mobile Number
                                </label>


                                <input
                                    type="text"
                                    name="mobileNo"
                                    value={
                                        formData.mobileNo
                                    }
                                    onChange={
                                        handleChange
                                    }
                                    placeholder="Mobile number"
                                />

                            </div>


                            {/* Phone */}

                            <div className="form-group">

                                <label>
                                    Phone Number
                                </label>


                                <input
                                    type="text"
                                    name="phoneNo"
                                    value={
                                        formData.phoneNo
                                    }
                                    onChange={
                                        handleChange
                                    }
                                    placeholder="Phone number"
                                />

                            </div>


                            {/* Website */}

                            <div className="form-group">

                                <label>
                                    Website
                                </label>


                                <input
                                    type="text"
                                    name="website"
                                    value={
                                        formData.website
                                    }
                                    onChange={
                                        handleChange
                                    }
                                    placeholder="www.example.com"
                                />

                            </div>

                        </div>

                    </div>


                    {/* =================================
                        BANK INFORMATION
                    ================================= */}

                    <div className="form-section">

                        <h3>
                            Bank Information
                        </h3>


                        <div className="form-grid">


                            {/* Bank Name */}

                            <div className="form-group">

                                <label>
                                    Bank Name
                                </label>


                                <input
                                    type="text"
                                    name="bankName"
                                    value={
                                        formData.bankName
                                    }
                                    onChange={
                                        handleChange
                                    }
                                    placeholder="Bank name"
                                />

                            </div>


                            {/* Account Number */}

                            <div className="form-group">

                                <label>
                                    Account Number
                                </label>


                                <input
                                    type="text"
                                    name="accountNumber"
                                    value={
                                        formData.accountNumber
                                    }
                                    onChange={
                                        handleChange
                                    }
                                    placeholder="Account number"
                                />

                            </div>


                            {/* IFSC */}

                            <div className="form-group">

                                <label>
                                    IFSC Code
                                </label>


                                <input
                                    type="text"
                                    name="ifscCode"
                                    value={
                                        formData.ifscCode
                                    }
                                    onChange={
                                        handleChange
                                    }
                                    placeholder="IFSC code"
                                />

                            </div>


                            {/* Branch */}

                            <div className="form-group">

                                <label>
                                    Branch
                                </label>


                                <input
                                    type="text"
                                    name="branch"
                                    value={
                                        formData.branch
                                    }
                                    onChange={
                                        handleChange
                                    }
                                    placeholder="Branch"
                                />

                            </div>

                        </div>

                    </div>


                    {/* =================================
                        TERMS
                    ================================= */}

                    <div className="form-section">

                        <h3>
                            Terms & Conditions
                        </h3>


                        <div className="form-group">

                            <textarea
                                name="termsAndConditions"
                                value={
                                    formData.termsAndConditions
                                }
                                onChange={
                                    handleChange
                                }
                                placeholder="Enter terms and conditions"
                                rows="5"
                            />

                        </div>

                    </div>


                    {/* =================================
                        ACTIVE STATUS
                    ================================= */}

                    {isEditMode && (

                        <div className="form-group">

                            <label>
                                Business Status
                            </label>


                            <label className="status-toggle">

                                <input
                                    type="checkbox"
                                    name="active"
                                    checked={
                                        formData.active
                                    }
                                    onChange={(e) =>
                                        setFormData({

                                            ...formData,

                                            active:
                                                e.target.checked

                                        })
                                    }
                                />


                                <span>

                                    {formData.active

                                        ? "Active"

                                        : "Inactive"

                                    }

                                </span>

                            </label>

                        </div>

                    )}


                    {/* =================================
                        BUTTONS
                    ================================= */}

                    <div className="business-form-actions">


                        <button
                            type="button"
                            className="business-cancel-button"
                            onClick={() =>
                                navigate("/business")
                            }
                            disabled={saving}
                        >

                            Cancel

                        </button>


                        <button
                            type="submit"
                            className="business-save-button"
                            disabled={saving}
                        >

                            {saving

                                ? "Saving..."

                                : isEditMode

                                    ? "Update Business"

                                    : "Create Business"

                            }

                        </button>


                    </div>


                </form>

            </div>


            {/* =================================
                SUCCESS / ERROR POPUP
            ================================= */}

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


                        {/* Button */}

                        <button
                            type="button"
                            className="popup-button"
                            onClick={closePopup}
                        >

                            OK

                        </button>


                    </div>

                </div>

            )}

        </div>

    );
}


export default BusinessForm;