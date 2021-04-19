USE forum;

DELIMITER //
CREATE TRIGGER authority_non_modifiables_trg BEFORE UPDATE ON authority FOR EACH ROW
BEGIN
	SET NEW.id = OLD.id;
    SET NEW.name = OLD.name;
END //

CREATE TRIGGER user_non_modifiables_trg BEFORE UPDATE ON user FOR EACH ROW
BEGIN
	SET NEW.id = OLD.id;
    SET NEW.username = OLD.username;
    SET NEW.join_time = OLD.join_time;
END //

CREATE TRIGGER user_insert_constraints_trg BEFORE INSERT ON user FOR EACH ROW
BEGIN
	IF character_length(NEW.username) < 5 THEN
		signal SQLSTATE '45000'
         SET message_text = 'Username cannot be shorter than 5 characters';
    END IF;
    
    SET NEW.join_time = current_timestamp();
END //

CREATE TRIGGER forum_non_modifiables_trg BEFORE UPDATE ON forum FOR EACH ROW
BEGIN
	SET NEW.id = OLD.id;
    SET NEW.create_time = OLD.create_time;
END //

CREATE TRIGGER forum_insert_constraints_trg BEFORE INSERT ON forum FOR EACH ROW
BEGIN
	IF character_length(NEW.title) = 0 THEN
		signal SQLSTATE '45000'
			SET message_text = 'Forum title cannot be empty';
    END IF;

	IF character_length(NEW.description) = 0 THEN
		signal SQLSTATE '45000'
			SET message_text = 'Forum description cannot be empty';
    END IF;
    
    SET NEW.create_time = current_timestamp();
END //

CREATE TRIGGER forum_update_constraints_trg BEFORE UPDATE ON forum FOR EACH ROW FOLLOWS forum_non_modifiables_trg
BEGIN
	IF (NEW.title IS NOT NULL) AND (character_length(NEW.title) = 0) THEN
		signal SQLSTATE '45000'
			SET message_text = 'Forum title cannot be empty';
    END IF;
    
    IF NEW.title IS NULL THEN
		SET NEW.title = OLD.title;
    END IF;

	IF (NEW.description IS NOT NULL) AND (character_length(NEW.description) = 0) THEN
		signal SQLSTATE '45000'
			SET message_text = 'Forum description cannot be empty';
    END IF;
    
    IF NEW.description IS NULL THEN
		SET NEW.description = OLD.description;
    END IF;
    
    IF (NEW.parent_forum_id IS NULL) AND ((SELECT COUNT(id) FROM thread WHERE forum_id = OLD.id) > 0) THEN
		signal SQLSTATE '45000'
			SET message_text = 'Cannot turn forum to be main forum because this forum contains threads';
    END IF;
    
END //


CREATE TRIGGER forum_moderators_non_modifiables_trg BEFORE UPDATE ON forum_moderators FOR EACH ROW
BEGIN
	SET NEW.user_id = OLD.user_id;
    SET NEW.forum_id = OLD.forum_id;
END //


CREATE TRIGGER thread_non_modifiables_trg BEFORE UPDATE ON thread FOR EACH ROW
BEGIN
	SET NEW.id = OLD.id;
    SET NEW.creator_id = OLD.creator_id;
    SET NEW.create_time = OLD.create_time;
END //

CREATE TRIGGER thread_insert_constraints_trg BEFORE INSERT ON thread FOR EACH ROW
BEGIN
	IF character_length(NEW.title) = 0 THEN
		signal SQLSTATE '45000'
			SET message_text = 'Thread title cannot be empty';
    END IF;

	IF character_length(NEW.message) = 0 THEN
		signal SQLSTATE '45000'
			SET message_text = 'Thread message cannot be empty';
    END IF;
    
    IF (SELECT parent_forum_id FROM forum WHERE id = NEW.forum_id) IS NULL THEN
		signal SQLSTATE '45000'
			SET message_text = 'Cannot add thread to main forum';
    END IF;
    
    SET NEW.create_time = current_timestamp();
END //

CREATE TRIGGER thread_update_constraints_trg BEFORE UPDATE ON thread FOR EACH ROW FOLLOWS thread_non_modifiables_trg
BEGIN
	IF (NEW.title IS NOT NULL) AND (character_length(NEW.title) = 0) THEN
		signal SQLSTATE '45000'
			SET message_text = 'Forum title cannot be empty';
    END IF;
    
    IF NEW.title IS NULL THEN
		SET NEW.title = OLD.title;
    END IF;

	IF (NEW.message IS NOT NULL) AND (character_length(NEW.message) = 0) THEN
		signal SQLSTATE '45000'
			SET message_text = 'Thread message cannot be empty';
    END IF;
    
    IF NEW.message IS NULL THEN
		SET NEW.message = OLD.message;
    END IF;
    
    IF (SELECT parent_forum_id FROM forum WHERE id = NEW.forum_id) IS NULL THEN
		signal SQLSTATE '45000'
			SET message_text = 'Cannot move thread to main forum';
    END IF;
END //

CREATE TRIGGER thread_likes_non_modifiables_trg BEFORE UPDATE ON thread_likes FOR EACH ROW
BEGIN
	SET NEW.user_id = OLD.user_id;
    SET NEW.thread_id = OLD.thread_id;
END //

CREATE TRIGGER post_non_modifiables_trg BEFORE UPDATE ON post FOR EACH ROW
BEGIN
	SET NEW.id = OLD.id;
    SET NEW.thread_id = OLD.thread_id;
    SET NEW.creator_id = OLD.creator_id;
    SET NEW.create_time = OLD.create_time;
END //

CREATE TRIGGER post_insert_constraints_trg BEFORE INSERT ON post FOR EACH ROW
BEGIN

	IF character_length(NEW.message) = 0 THEN
		signal SQLSTATE '45000'
			SET message_text = 'Post message cannot be empty';
    END IF;
    
    SET NEW.create_time = current_timestamp();
END //

CREATE TRIGGER post_update_constraints_trg BEFORE UPDATE ON post FOR EACH ROW FOLLOWS post_non_modifiables_trg
BEGIN
	IF (NEW.message IS NOT NULL) AND (character_length(NEW.message) = 0) THEN
		signal SQLSTATE '45000'
			SET message_text = 'Post message cannot be empty';
    END IF;
    
    IF NEW.message IS NULL THEN
		SET NEW.message = OLD.message;
    END IF;
END //

CREATE TRIGGER post_likes_non_modifibles_trg BEFORE UPDATE ON post_likes FOR EACH ROW
BEGIN
	SET NEW.post_id = OLD.post_id;
    SET NEW.user_id = OLD.user_id;
END //

DELIMITER ;
