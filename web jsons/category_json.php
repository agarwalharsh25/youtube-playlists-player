<?php

	require '../connect.php';

	// array for JSON response
	$response = array();
	$response["categories"] = array();

	$select = "SELECT `id`, `category` FROM `categories`";
	
	if ( $stmt = $conn->prepare( $select ) ) {
        	if ( $stmt->execute() ) {
	            $stmt->bind_result( $id, $category );
	            
	            while($stmt->fetch()) {
	                $categories = array();
	                
	            	$categories["id"] = $id;
	            	$categories["category"] = $category;

	            	array_push($response["categories"], $categories);
	            }
	        }
	}

	echo json_encode($response);
?>