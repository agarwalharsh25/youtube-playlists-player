<?php
	require '../connect.php';

	// array for JSON response
	$response = array();
	$response["videos"] = array();

	$select = "SELECT v.`video_id`, v.`title`, v.`playlist_id`, v.`description`, v.`publish_date`, v.`views`, v.`thumbnail`, p.`playlist`, p.`playlist_image`, p.`category_id`, c.`category` FROM `videos` v, `playlists` p, `categories` c WHERE v.playlist_id=p.id AND p.category_id=c.id ORDER BY v.`views` DESC LIMIT 10";
	
	if ( $stmt = $conn->prepare( $select ) ) {
        	if ( $stmt->execute() ) {
	            $stmt->bind_result( $video_id, $title, $playlist_id, $description, $publish_date, $views, $thumbnail, $playlist_name, $playlist_image, $category_id, $category_name );
	            while($stmt->fetch()) {
	                $videos = array();
	            	$videos["video_id"] = $video_id;
	            	$videos["title"] = $title;
	            	$videos["playlistId"] = $playlist_id;
	            	$videos["description"] = $description;
	            	$videos["publishDate"] = $publish_date;
	            	$videos["views"] = $views;
	            	$videos["thumbnail"] = $thumbnail;
	            	$videos["playlistName"] = $playlist_name;
	            	$videos["playlistImage"] = $playlist_image;
	            	$videos["categoryId"] = $category_id;
	            	$videos["categoryName"] = $category_name;

	            	array_push($response["videos"], $videos);
	            }
	        }
	}

	echo json_encode($response);
?>