using System;
using System.Configuration;
using System.Collections.Generic;
using System.Linq;
using System.Web;
using System.Web.UI;
using System.Web.UI.WebControls;
using System.Data.SqlClient;


/// <summary>
/// Summary description for HealthClub
/// </summary>
public partial class _Default : System.Web.UI.Page
{
    private SqlConnection connection;
    private string connectionString = "Server=acadmssql.asu.edu; Database=; User Id=; password=";

    protected void Page_Load(object sender, EventArgs e)
    {
        if (!IsPostBack)
        {
            populateDropdown();
        }
    }

    protected void sendButton_Click(object sender, EventArgs e)
    {
        getPatientInfo();
        getVisits();
    }

    public void populateDropdown()
    {
        connection = new SqlConnection(connectionString);
        SqlCommand getEmpListCMD = new SqlCommand("SELECT patientID, (lastName + ', ' + firstName) AS name FROM patient ORDER BY name", connection);
        connection.Open();


        SqlDataReader dropDownListValues = getEmpListCMD.ExecuteReader();

        patientList.DataSource = dropDownListValues;
        patientList.DataValueField = "patientID";
        patientList.DataTextField = "name";
        patientList.DataBind();

        dropDownListValues.Close();
        connection.Close();
    }
    
    public void getPatientInfo()
    {
        connection = new SqlConnection(connectionString);
        SqlCommand getPatientInfoCMD = new SqlCommand("SELECT (p.lastName + ', ' + p.firstName) AS 'Patient Name', p.dateOfBirth AS 'Date of Birth', p.accountID AS Account, a.balance AS Balance, a.responsibleParty AS 'Responsible Party' FROM patient p, account a WHERE p.accountID = a.accountID AND p.patientID = '" + patientList.SelectedValue + "'", connection);
        connection.Open();
        SqlDataReader patientReader = getPatientInfoCMD.ExecuteReader();

        patientInfo.DataSource = patientReader;
        patientInfo.DataBind();

        patientReader.Close();
        connection.Close();
    }

    public void getVisits()
    {
        connection = new SqlConnection(connectionString);
        String chosenAccount = patientList.SelectedValue;
        SqlCommand getVisitInfoCMD = new SqlCommand("select Distinct v.visitDate as Date, doc.drName as Doctor, v.charge as Charge, v.description as Description, (select (d.dDescription + '; ') from  visitDiagnosis vd, diagnosis d where d.dCode = vd.dCode and vd.patientID = v.patientID and vd.visitDate = v.visitDate order by d.dDescription FOR XML PATH('')) AS Diagnoses from visit v, doctor doc, visitDiagnosis vd where v.DOCTORID = doc.DOCTORID and v.PATIENTID = vd.PATIENTID and v.visitDate = vd.visitDate and vd.patientID ='" + patientList.SelectedValue + "' order by v.visitDate desc", connection);

        connection.Open();
        SqlDataReader visitReader = getVisitInfoCMD.ExecuteReader();

        visitGrid.DataSource = visitReader;
        visitGrid.DataBind();

        visitReader.Close();
        connection.Close();
    }
}