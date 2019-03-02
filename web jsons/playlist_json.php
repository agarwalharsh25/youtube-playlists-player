<?php
	$cat=$_GET['cat'];
	require '../connect.php';

	// array for JSON response
	$response = array();
	$response["playlists"] = array();

	$select = "SELECT p.`id`, p.`category_id`, p.`playlist`, p.`playlist_image`, c.`category` FROM `playlists` p, `categories` c WHERE `category_id` LIKE ? AND p.category_id=c.id ";
	
	if ( $stmt = $conn->prepare( $select ) ) {
        if ( $stmt->bind_param( 'i', $cat ) ) {
        	if ( $stmt->execute() ) {
	            $stmt->bind_result( $id, $category_id, $playlist, $playlist_image, $category );
	            while($stmt->fetch()) {
	                $playlists = array();
	            	$playlists["id"] = $id;
	            	$playlists["playlistName"] = $playlist;
	            	$playlists["playlistImage"] = $playlist_image;
	            	$playlists["categoryId"] = $category_id;
	            	$playlists["categoryName"] = $category;

	            	array_push($response["playlists"], $playlists);
	            }
	        }
	    }
	}

	echo json_encode($response);
?>