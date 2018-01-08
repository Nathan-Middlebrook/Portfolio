<html>
    <title>Delete Form</title>
    <body>
        <div align="center" id="main">
            <h1>Delete Form</h1>
            <div align="center">
                <h2>Patient</h2>
                <form method="post">
                    <label>Patient ID :</label>
                    <input type="text" name="patientID" id="pid" required="required" placeholder="Please Enter Patient ID"/><br /><br />
                    <input type="submit" value="Delete" name="DeletePatient"/><br /><br />
                </form>
                <h2>Visit</h2>
                <form method="post">
                    <label>Patient ID :</label>
                    <input type="text" name="patientID" id="vpid" required="required" placeholder="Please Enter Patient ID"/><br /><br />
                    <label>Visit Date :</label>
                    <input type="text" name="visitDate" id="vdate" required="required" placeholder="Please Enter Visit Date"/><br /><br />
                    <input type="submit" value="Delete" name="DeleteVisit"/><br /><br />
                </form>
                <input type="submit" value="Back"  name = "back" onclick="window.location.href='MetaWebDBPatient.php'"/><br /><br />
            </div>
        </div>

        <?php
            /* Attempt MySQL server connection. Assuming you are running MySQL
            server with default setting (user 'root' with no password) */
            if(isset($_POST["DeletePatient"]))
            {
                $pid = $_POST['patientID'];
                $con = mysqli_connect("localhost", "root", "", "patient");
 
                // Check connection
                if($con === false)
                {
                    die("Connection failed: " . mysqli_connect_error());
                }
                
                $sql = "DELETE FROM patient WHERE patientID = '".$pid."'";

                if(mysqli_query($con, $sql))
                {
                    echo "<script type= 'text/javascript'>alert('Patient Deleted');</script>";
                }       
                else  
                {
                    echo "<script type= 'text/javascript'>alert('Error: " . $sql . "<br>" . $conn->error."');</script>";
                }
                // Close connection
                mysqli_close($con);
            }
            
            if(isset($_POST["DeleteVisit"]))
            {
                $pid = $_POST['patientID'];
                $vdate = $_POST['visitDate'];
                $con = mysqli_connect("localhost", "root", "", "patient");
 
                // Check connection
                if($con === false)
                {
                    die("Connection failed: " . mysqli_connect_error());
                }
                
                $sql = "DELETE FROM visit WHERE patientID = '".$pid."' AND visitDate = '".$vdate."'";
                $vdsql = "DELETE FROM visitdiagnosis WHERE patientID = '".$pid."' AND visitDate = '".$vdate."'";
                if(mysqli_query($con, $sql) && mysqli_query($con, $vdsql))
                {
                    echo "<script type= 'text/javascript'>alert('Visit Deleted');</script>";
                }       
                else  
                {
                    echo "<script type= 'text/javascript'>alert('Error: " . $sql . "<br>" . $conn->error."');</script>";
                }
                // Close connection
                mysqli_close($con);
            }
        ?>
    </body>
</html>
