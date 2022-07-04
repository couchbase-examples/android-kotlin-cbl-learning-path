function sync(doc, oldDoc) {

	/* Data Validation */
	validateNotEmpty("documentType", doc.documentType);

	if (doc.documentType == 'warehouse') {
		console.log("********Processing Warehouse Docs - setting it to global/public");
		channel('!');
	} else {
		console.log("********Processing Team Docs");
		validateNotEmpty("team", doc.team);
		if (!isDelete()) {

			/* Routing  -- add channel routing rules here for document */
			var team = getTeam();
			var channelId = "channel." + team;
			console.log("********Setting Channel to " + channelId);
			channel(channelId);

			/* Authorization  - Access Control */
			requireRole(team);
			access("role:team1", "channel.team1");
			access("role:team2", "channel.team2");
			access("role:team3", "channel.team3");
			access("role:team4", "channel.team4");
			access("role:team5", "channel.team5");
			access("role:team6", "channel.team6");
			access("role:team7", "channel.team7");
			access("role:team8", "channel.team8");
			access("role:team9", "channel.team9");
			access("role:team10", "channel.team10");
		}
	}
	// get type property
	function getType() {
		return (isDelete() ? oldDoc.documentType : doc.documentType);
	}

	// get email Id property
	function getTeam() {
		return (isDelete() ? oldDoc.team : doc.team);
	}

	// Check if document is being created/added for first time
	function isCreate() {
		// Checking false for the Admin UI to work
		return ((oldDoc == false) || (oldDoc == null || oldDoc._deleted) && !isDelete());
	}

	// Check if this is a document delete
	function isDelete() {
		return (doc._deleted == true);
	}

	// Verify that specified property exists
	function validateNotEmpty(key, value) {
		if (!value) {
			throw ({ forbidden: key + " is not provided." });
		}
	}
}