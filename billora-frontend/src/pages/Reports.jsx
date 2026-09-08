import { useEffect, useState } from "react";
import api from "../api/axios";
import "../css/Reports.css";

function Reports() {

    const [businesses, setBusinesses] = useState([]);

    const [selectedBusiness, setSelectedBusiness] = useState("");

    const [reportType, setReportType] = useState("");

    const [format, setFormat] = useState("pdf");

    const [startDate, setStartDate] = useState("");

    const [endDate, setEndDate] = useState("");

    const [loadingBusinesses, setLoadingBusinesses] = useState(true);

    const [generating, setGenerating] = useState(false);

    const [error, setError] = useState("");

    const [message, setMessage] = useState("");


    // =========================================
    // LOAD BUSINESSES
    // =========================================

    useEffect(() => {

        loadBusinesses();

    }, []);


    const loadBusinesses = async () => {

        try {

            setLoadingBusinesses(true);

            const response =
               await api.get(
                    "/business"
                );
            setBusinesses(response.data);

        } catch (error) {

            console.error(
                "Failed to load businesses:",
                error
            );

            setError(
                "Unable to load businesses."
            );

        } finally {

            setLoadingBusinesses(false);
        }
    };


    // =========================================
    // BUSINESS CHANGE
    // =========================================

    const handleBusinessChange = (e) => {

        setSelectedBusiness(e.target.value);

        // Reset report selections
        setReportType("");

        setStartDate("");

        setEndDate("");

        setMessage("");

        setError("");
    };


    // =========================================
    // REPORT TYPE CHANGE
    // =========================================

    const handleReportTypeChange = (e) => {

        setReportType(e.target.value);

        setStartDate("");

        setEndDate("");

        setMessage("");

        setError("");
    };


    // =========================================
    // GENERATE REPORT
    // =========================================

    const generateReport = async () => {

        setError("");

        setMessage("");


        // Business validation

        if (!selectedBusiness) {

            setError(
                "Please select a business first."
            );

            return;
        }


        // Report validation

        if (!reportType) {

            setError(
                "Please select a report type."
            );

            return;
        }


        // Custom date validation

        if (
            reportType === "custom"
            && (!startDate || !endDate)
        ) {

            setError(
                "Please select both start date and end date."
            );

            return;
        }


        if (
            reportType === "custom"
            && startDate > endDate
        ) {

            setError(
                "Start date cannot be greater than end date."
            );

            return;
        }


        try {

            setGenerating(true);


            // =========================================
            // BUILD PARAMETERS
            // =========================================

            const params = {

                format: format,

                businessId: selectedBusiness
            };


            if (reportType === "custom") {

                params.startDate = startDate;

                params.endDate = endDate;
            }


            // =========================================
            // API CALL
            // =========================================

            const response = await api.get(

                `/reports/generate/${reportType}`,

                {
                    params: params,

                    responseType: "blob"
                }

            );


            // =========================================
            // CREATE DOWNLOAD
            // =========================================

            const blob = new Blob(
                [response.data],
                {
                    type:
                        format === "pdf"
                            ? "application/pdf"
                            : "application/vnd.openxmlformats-officedocument.spreadsheetml.sheet"
                }
            );


            const url =
                window.URL.createObjectURL(blob);


            const link =
                document.createElement("a");


            link.href = url;


            const extension = "pdf";


            link.download =
                `invoice-report-${reportType}.${extension}`;


            document.body.appendChild(link);

            link.click();

            link.remove();


            window.URL.revokeObjectURL(url);


            setMessage(
                "Report generated successfully."
            );


        } catch (error) {

            console.error(
                "Report generation failed:",
                error
            );


            setError(
                "Failed to generate report. Please try again."
            );

        } finally {

            setGenerating(false);
        }
    };


    return (

        <div className="reports-page">


            {/* =========================================
                HEADER
            ========================================= */}

            <div className="reports-header">

                <div>

                    <h1>
                        GST Reports
                    </h1>

                    <p>
                        Generate invoice reports for your business
                    </p>

                </div>

            </div>


            {/* =========================================
                ERROR
            ========================================= */}

            {error && (

                <div className="reports-error">

                    <span>!</span>

                    {error}

                </div>

            )}


            {/* =========================================
                SUCCESS
            ========================================= */}

            {message && (

                <div className="reports-success">

                    <span>✓</span>

                    {message}

                </div>

            )}


            {/* =========================================
                REPORT CARD
            ========================================= */}

            <div className="reports-card">


                <div className="reports-card-header">

                    <div>

                        <h2>
                            Generate Report
                        </h2>

                        <span>
                            Select the business and report details
                        </span>

                    </div>

                </div>


                <div className="reports-form">


                    {/* =================================
                        STEP 1 - BUSINESS
                    ================================= */}

                    <div className="report-step">

                        <div className="step-number">
                            1
                        </div>

                        <div className="step-content">

                            <label>
                                Select Business
                            </label>

                            <select
                                value={selectedBusiness}
                                onChange={
                                    handleBusinessChange
                                }
                                disabled={
                                    loadingBusinesses
                                }
                            >

                                <option value="">
                                    {loadingBusinesses
                                        ? "Loading businesses..."
                                        : "Select Business"}
                                </option>


                                {businesses.map(
                                    (business) => (

                                        <option
                                            key={business.id}
                                            value={business.id}
                                        >
                                            {business.businessName}
                                        </option>

                                    )
                                )}

                            </select>

                            <small>
                                Select the business for which
                                you want to generate the report.
                            </small>

                        </div>

                    </div>


                    {/* =================================
                        STEP 2 - REPORT TYPE
                    ================================= */}

                    <div
                        className={`report-step ${
                            !selectedBusiness
                                ? "disabled-step"
                                : ""
                        }`}
                    >

                        <div className="step-number">
                            2
                        </div>

                        <div className="step-content">

                            <label>
                                Report Type
                            </label>

                            <select
                                value={reportType}
                                onChange={
                                    handleReportTypeChange
                                }
                                disabled={
                                    !selectedBusiness
                                }
                            >

                                <option value="">
                                    Select Report Type
                                </option>

                                <option value="monthly">
                                    Monthly Report
                                </option>

                                <option value="quarterly">
                                    Quarterly Report
                                </option>

                                <option value="annually">
                                    Annual Report
                                </option>

                                <option value="custom">
                                    Custom Date Report
                                </option>

                            </select>

                        </div>

                    </div>


                    {/* =================================
                        CUSTOM DATE
                    ================================= */}

                    {reportType === "custom" && (

                        <div className="custom-date-section">

                            <div className="date-group">

                                <label>
                                    Start Date
                                </label>

                                <input
                                    type="date"
                                    value={startDate}
                                    onChange={(e) =>
                                        setStartDate(
                                            e.target.value
                                        )
                                    }
                                />

                            </div>


                            <div className="date-group">

                                <label>
                                    End Date
                                </label>

                                <input
                                    type="date"
                                    value={endDate}
                                    onChange={(e) =>
                                        setEndDate(
                                            e.target.value
                                        )
                                    }
                                />

                            </div>

                        </div>

                    )}


                    {/* =================================
                        STEP 3 - FORMAT
                    ================================= */}

                    <div
                        className={`report-step ${
                            !reportType
                                ? "disabled-step"
                                : ""
                        }`}
                    >

                        <div className="step-number">
                            3
                        </div>

                        <div className="step-content">

                            <label>
                                Report Format
                            </label>

                            <div className="format-options">


                                <label
                                    className={`format-option ${
                                        format === "pdf"
                                            ? "selected"
                                            : ""
                                    }`}
                                >

                                    <input
                                        type="radio"
                                        name="format"
                                        value="pdf"
                                        checked={
                                            format === "pdf"
                                        }
                                        onChange={(e) =>
                                            setFormat(
                                                e.target.value
                                            )
                                        }
                                        disabled={
                                            !reportType
                                        }
                                    />

                                    <div>

                                        <strong>
                                            PDF
                                        </strong>

                                        <span>
                                            Printable document
                                        </span>

                                    </div>

                                </label>

                            </div>

                        </div>

                    </div>


                    {/* =================================
                        ACTION
                    ================================= */}

                    <div className="report-actions">

                        <button
                            className="generate-report-button"
                            onClick={generateReport}
                            disabled={
                                generating ||
                                !selectedBusiness ||
                                !reportType
                            }
                        >

                            {generating
                                ? "Generating..."
                                : "Generate Report"}

                        </button>

                    </div>

                </div>

            </div>


            {/* =========================================
                INFORMATION
            ========================================= */}

            <div className="report-info">

                <div className="info-icon">
                    i
                </div>

                <div>

                    <strong>
                        Report Information
                    </strong>

                    <p>
                        Reports are generated using the
                        invoices available for the selected
                        business and date range.
                    </p>

                </div>

            </div>

        </div>
    );
}

export default Reports;