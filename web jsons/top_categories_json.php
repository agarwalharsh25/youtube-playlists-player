<?php
	require '../connect.php';

	// array for JSON response
	$response = array();
	$response["categories"] = array();

	$select = "SELECT SUM(v.`views`) AS total_views, c.`id`, c.`category` FROM `playlists` p, `categories` c, `videos` v WHERE p.category_id=c.id AND p.id=v.playlist_id GROUP BY p.category_id ORDER BY total_views DESC LIMIT 10";
	
	if ( $stmt = $conn->prepare( $select ) ) {
        	if ( $stmt->execute() ) {
	            $stmt->bind_result( $total_views, $id, $category );
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