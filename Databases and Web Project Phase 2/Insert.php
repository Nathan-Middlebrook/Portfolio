<html>
    <head>
        <title>Insert Form</title>
    </head>
    <body>
        <div align ="center" id="main">
            <h1 align="center">Insert Form</h1>
            <div align="center">
                <h2 align="center">Patient</h2>
                <form method="post">
                    <label>Patient ID :</label>
                    <input type="text" name="patientID" id="id" required="required" placeholder="Please Enter Patient ID"/><br /><br />
                    <label>First Name :</label>
                    <input type="text" name="firsttName" id="fname" required="required" placeholder="Please Enter First Name"/><br/><br />
                    <label>Last Name :</label>
                    <input type="text" name="lastName" id="lname" required="required" placeholder="Please Enter Last Name"/><br/><br />
                    <label>Date of Birth :</label>
                    <input type="text" name="dateofbirth" id="birthd" required="required" placeholder="Please Enter Birth Date"/><br/><br />
                    <label>Account ID :</label>
                    <select name="accountID"><br/>
                        <option value="a001">a001</option><br />
                        <option value="a002">a002</option><br />
                        <option value="a003">a003</option><br />
                        <option value="a004">a004</option><br />
                        <option value="a005">a005</option><br />
                        <option value="a006">a006</option><br />
                        <option value="a007">a007</option><br />
                    </select><br/><br/>
                    <input type="submit" value="Insert" name="InsertPatient"/><br />
                </form>
                <h2 align="center">Visit</h2>
                <form method="post">
                    <label>Charge :</label>
                    <input type="text" name="charge" id="charge" required="required" placeholder="Please Enter Charge"/><br /><br />
                    <label>Description :</label>
                    <select name="description"><br/>
                        <option value="Chest pain">Chest pain</option><br />
                        <option value="Headaches">Headaches</option><br />
                        <option value="Sore throat">Sore throat</option><br />
                        <option value="Lack of energy">Lack of energy</option><br />
                        <option value="Congestion">Congestion</option><br />
                        <option value="Frequent urination">Frequent urination</option><br />
                        <option value="Sports Physical">Sports Physical</option><br />
                    </select><br/><br/>
                    <label>Doctor ID :</label>
                    <select name="doctorID"><br/>
                        <option value="dr01">dr01</option><br />
                        <option value="dr02">dr02</option><br />
                        <option value="dr03">dr03</option><br />
                        <option value="dr04">dr04</option><br />
                    </select><br/><br/>
                    <label>Patient ID :</label>
                    <input type="text" name="patientID" id="pid" required="required" placeholder="Please Enter Patient ID"/><br/><br />
                    <label>Visit Date :</label>
                    <input type="text" name="visitDate" id="vdate" required="required" placeholder="Please Enter Visit Date"/><br/><br />
                    <label>Diagnosis :</label>
                    <select name="diagnosis"><br/>
                        <option value="Sinusitis">Sinusitis</option><br />
                        <option value="High blood pressure">High blood pressure</option><br />
                        <option value="Heart disease">Heart disease</option><br />
                        <option value="Diabetes">Diabetes</option><br />
                        <option value="Allergies">Allergies</option><br />
                        <option value="Bronchitis">Bronchitis</option><br />
                        <option value="Migraine">Migraine</option><br />
                        <option value="Strep throat">Strep throat</option><br />
                        <option value="Physical">Physical</option><br />
                    </select><br/><br/>
                    <input type="submit" value="Insert" name="InsertVisit"/><br />
                </form>
                <input type="submit" value=" Back "  name = "back" onclick="window.location.href='MetaWebDBPatient.php'"/><br /><br />
            </div>
        </div>
        <?php            
            $servername = "localhost";
            $username = "root";
            $password = "";
            $dbname = "patient";

            // Create connection
            $conn = new mysqli($servername, $username, $password, $dbname);
		
            // Check connection
            if (!$conn) 
            {
                die("Connection failed: " . mysqli_connect_error());
            }

            if(isset($_POST["InsertPatient"]))
            {
                $sql = "INSERT INTO patient (PATIENTID,LASTNAME,FIRSTNAME,DATEOFBIRTH,ACCOUNTID)
                VALUES ('".$_POST["patientID"]."','".$_POST["lastName"]."','".$_POST["firsttName"]."','".$_POST["dateofbirth"]."','".$_POST["accountID"]."')";

                if ($conn->query($sql) === TRUE)
                {
                    echo "<script type= 'text/javascript'>alert('Patient Added');</script>";
                }
                else
                {
                    echo "<script type= 'text/javascript'>alert('Error: " . $sql . "<br>" . $conn->error."');</script>";
                }
                $conn->close();
            }
            if(isset($_POST["InsertVisit"]))
            {
                $sql = "INSERT INTO visit (CHARGE,DESCRIPTION,DOCTORID,PATIENTID,VISITDATE)
                VALUES ('".$_POST["charge"]."','".$_POST["description"]."','".$_POST["doctorID"]."','".$_POST["patientID"]."','".$_POST["visitDate"]."')";
                
                $diag = $_POST['diagnosis'];
                $getdcode = "SELECT dcode FROM diagnosis WHERE dDescription = '".$diag."'";
                if($dcode = $conn->Query($getdcode))
                {
                    $row = $dcode->fetch_array(MYSQLI_NUM);
                }
                $vdsql = "INSERT INTO visitdiagnosis (PATIENTID,VISITDATE,DCODE)
                VALUES ('".$_POST['patientID']."','".$_POST['visitDate']."','".$row[0]."')";
                
                if ($conn->query($sql) === TRUE && $conn->query($vdsql) === TRUE)
                {
                    echo "<script type= 'text/javascript'>alert('Visit Added');</script>";
                }
                else
                {
                    echo "<script type= 'text/javascript'>alert('Error: " . $sql . "<br>" . $conn->error."');</script>";
                }
                $conn->close();
            }
        ?>
    </body>
</html>