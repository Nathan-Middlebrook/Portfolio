<HTML>
    <head>
        <title> Patient Visit Information </title>
    </head>
    <body style="background-color:white">
	<h1 align="center"> Patient Visit Information </h1>
        <div align="center">
            <form>
                <input type="button" value="Insert" onclick="window.location.href='http://127.0.0.1/edsa-Patient/Insert.php'" />
                <input type="button" value="Delete" onclick="window.location.href='http://127.0.0.1/edsa-Patient/Delete.php'" />
            </form>
        </div>
	<div align="center">
        <?php
            print('<form action="MetaWebDBPatient.php" method="post">
		<select name="patientID" id="patientID">');
            $servername = "localhost";
            $username = "root";
            $password = "";
            $dbname = "patient";

            // Create connection
            $mysqli = mysqli_connect($servername, $username, $password, $dbname);
		
            // Check connection
            if (!$mysqli) 
            {
		die("Connection failed: " . mysqli_connect_error());
            }
            $ddQuery = 'SELECT * FROM patient order by lastname';
            $stmt = 'SELECT p.patientID, p.lastName, p.firstName, p.dateOfBirth, p.accountID, a.balance, a.responsibleParty	FROM patient p join account a on p.accountID = a.accountID WHERE p.patientID = "'.$_POST['patientID'].'"';
            $tableQuery = 'SELECT DISTINCT v.visitDate, d.drName, v.charge, v.description, GROUP_CONCAT(CONCAT(di.dDescription) Separator \', \') as diagnosises FROM patient p join visit v on p.patientID = v.patientID join visitDiagnosis vd on v.patientID = vd.patientID join doctor d on v.doctorID = d.doctorID join diagnosis di on vd.dCode = di.dCode WHERE p.patientID = "'.$_POST['patientID'].'" AND v.visitDate = vd.visitDate GROUP BY v.visitDate, d.drName, v.charge, v.description ORDER BY v.visitDate DESC';

            if ($result = $mysqli->query($ddQuery))	
            {
                // Populate drop down list with lastname and firstname
                while ($row = $result->fetch_array(MYSQLI_NUM))
                {
                    printf('<option value="'.$row[0].'">%1$s, %2$s</option>', $row[1], $row[2]);
                }
            }

            print('</select><input type="submit" value="Select Patient" name="submit"/></form>');
            print ("<br></br>");
					
            if ($result2 = $mysqli->query($stmt))
            {
                while ($row = $result2->fetch_array(MYSQLI_NUM))
                {		
                    ?>Patient Name: <?php print($row[1] . ", " . $row[2]);?><br/>
                    Date of Birth: <?php print($row[3]);?><br/>
                    Account ID: <?php print($row[4]);?><br/>
                    Balance: <?php print($row[5]);?><br/>
                    Responsible Party: <?php print($row[6]);
		}
            }
        ?>
        <h1 align="center"> Visit </h1>
        <?php 
            print('<table style="table-layout: fixed; width: 80%" border="1" align="center">');
            print('<tr>
		<th>Date</th>
		<th>Doctor</th>
		<th>Charge</th>
		<th>Description</th>
		<th>Diagnosis</th>
		</tr>');
			
            if ($result = $mysqli->query($tableQuery))	
            {
                while ($row = $result->fetch_array(MYSQLI_NUM))
		{
                    print('<tr><td align="center">'.$row[0].'</td>'.'<td align="center">'.$row[1].'</td><td align="center">'.$row[2].'</td><td align="center">'.$row[3].'</td><td align="center">'.$row[4].'</td></tr>');
		}
            }
            mysqli_close($mysqli);
        ?>
    </body>
</html>