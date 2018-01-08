<%@ Page Language="C#" AutoEventWireup="true" Debug="true" CodeFile="Default.aspx.cs" Inherits="_Default" %>

<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Transitional//EN" "http://www.w3.org/TR/xhtml1/DTD/xhtml1-transitional.dtd">

<html xmlns="http://www.w3.org/1999/xhtml">
<head id="Head1" runat="server">
    <title></title>
</head>
<body>
    <form id="form1" runat="server">
    <div align="center" style="height: 544px">
    
        Patients:
        <br />
        <br />
        <asp:DropDownList ID="patientList" runat="server">
        </asp:DropDownList>
        <br />
        <br />
        <asp:Button ID="sendButton" runat="server" Text="Send" 
            onclick="sendButton_Click" />
        <br />
        <br />
        Patient Information:
        <asp:GridView ID="patientInfo" runat="server">
        </asp:GridView>
        <br />
        <br />
        Visits:
        <asp:GridView ID="visitGrid" runat="server">
        </asp:GridView>
    </div>
    </form>
</body>
</html>
