<?php
	require '../connect.php';

	// array for JSON response
	$response = array();
	$response["playlists"] = array();

	$select = "SELECT SUM(v.`views`) AS total_views, p.`id`, p.`category_id`, p.`playlist`, p.`playlist_image`, c.`category` FROM `playlists` p, `categories` c, `videos` v WHERE p.category_id=c.id AND p.id=v.playlist_id GROUP BY v.playlist_id ORDER BY total_views DESC LIMIT 10";
	
	if ( $stmt = $conn->prepare( $select ) ) {
        	if ( $stmt->execute() ) {
	            $stmt->bind_result( $total_views, $id, $category_id, $playlist, $playlist_image, $category );
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

	echo json_encode($response);
?>