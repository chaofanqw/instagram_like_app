package net.dunrou.mobile.base.message;


import net.dunrou.mobile.base.firebaseClass.FirebaseEventComment;
import net.dunrou.mobile.base.firebaseClass.FirebaseEventLike;
import net.dunrou.mobile.base.firebaseClass.FirebaseEventPost;
import net.dunrou.mobile.base.firebaseClass.FirebaseRelationship;
import net.dunrou.mobile.base.firebaseClass.FirebaseUser;

public class ActivityFeedMessage {

    public static class UserAddedEvent {

        private final FirebaseUser newUser;

        public UserAddedEvent(FirebaseUser newUser) {
            this.newUser = newUser;
        }

        public FirebaseUser getUser() {
            return newUser;
        }
    }

    public static class UserChangedEvent {

        private final FirebaseUser changedUser;

        public UserChangedEvent(FirebaseUser changedUser) {
            this.changedUser = changedUser;
        }

        public FirebaseUser getUser() {
            return changedUser;
        }
    }

    public static class UserRemovedEvent {

        private final FirebaseUser removedUser;

        public UserRemovedEvent(FirebaseUser removedUser) {
            this.removedUser = removedUser;
        }

        public FirebaseUser getUser() {
            return removedUser;
        }
    }

    public static class RelationshipAddedEvent {
        private FirebaseRelationship firebaseRelationship;
        public RelationshipAddedEvent(FirebaseRelationship firebaseRelationship) {
            this.firebaseRelationship = firebaseRelationship;
        }

        public FirebaseRelationship getFirebaseRelationship() {
            return firebaseRelationship;
        }
    }

    public static class RelationshipChangedEvent {
        private FirebaseRelationship firebaseRelationship;
        public RelationshipChangedEvent(FirebaseRelationship firebaseRelationship) {
            this.firebaseRelationship = firebaseRelationship;
        }

        public FirebaseRelationship getFirebaseRelationship() {
            return firebaseRelationship;
        }
    }

    public static class RelationshipRemovedEvent {
        private FirebaseRelationship firebaseRelationship;
        public RelationshipRemovedEvent(FirebaseRelationship firebaseRelationship) {
            this.firebaseRelationship = firebaseRelationship;
        }

        public FirebaseRelationship getFirebaseRelationship() {
            return firebaseRelationship;
        }
    }

    public static class PostAddedEvent {

        private final FirebaseEventPost newPost;

        public PostAddedEvent(FirebaseEventPost newPost) {
            this.newPost = newPost;
        }

        public FirebaseEventPost getPost() {
            return newPost;
        }
    }

    public static class PostChangedEvent {

        private final FirebaseEventPost newPost;

        public PostChangedEvent(FirebaseEventPost newPost) {
            this.newPost = newPost;
        }

        public FirebaseEventPost getPost() {
            return newPost;
        }
    }

    public static class PostRemovedEvent {

        private final FirebaseEventPost newPost;

        public PostRemovedEvent(FirebaseEventPost newPost) {
            this.newPost = newPost;
        }

        public FirebaseEventPost getPost() {
            return newPost;
        }
    }

    public static class AllPostAddedEvent {

        private final FirebaseEventPost newPost;

        public AllPostAddedEvent(FirebaseEventPost newPost) {
            this.newPost = newPost;
        }

        public FirebaseEventPost getPost() {
            return newPost;
        }
    }

    public static class AllPostChangedEvent {

        private final FirebaseEventPost newPost;

        public AllPostChangedEvent(FirebaseEventPost newPost) {
            this.newPost = newPost;
        }

        public FirebaseEventPost getPost() {
            return newPost;
        }
    }

    public static class AllPostRemovedEvent {

        private final FirebaseEventPost newPost;

        public AllPostRemovedEvent(FirebaseEventPost newPost) {
            this.newPost = newPost;
        }

        public FirebaseEventPost getPost() {
            return newPost;
        }
    }

    public static class LikeAddedEvent {

        private final FirebaseEventLike newLike;

        public LikeAddedEvent(FirebaseEventLike newLike) {
            this.newLike = newLike;
        }

        public FirebaseEventLike getLike() {
            return newLike;
        }
    }

    public static class LikeChangedEvent {

        private final FirebaseEventLike newLike;

        public LikeChangedEvent(FirebaseEventLike newLike) {
            this.newLike = newLike;
        }

        public FirebaseEventLike getLike() {
            return newLike;
        }
    }

    public static class LikeRemovedEvent {

        private final FirebaseEventLike newLike;

        public LikeRemovedEvent(FirebaseEventLike newLike) {
            this.newLike = newLike;
        }

        public FirebaseEventLike getLike() {
            return newLike;
        }
    }

    public static class CommentAddedEvent {

        private final FirebaseEventComment newPost;

        public CommentAddedEvent(FirebaseEventComment newPost) {
            this.newPost = newPost;
        }

        public FirebaseEventComment getPost() {
            return newPost;
        }
    }

    public static class CommentChangedEvent {

        private final FirebaseEventComment newPost;

        public CommentChangedEvent(FirebaseEventComment newPost) {
            this.newPost = newPost;
        }

        public FirebaseEventComment getPost() {
            return newPost;
        }
    }

    public static class CommentRemovedEvent {

        private final FirebaseEventComment newPost;

        public CommentRemovedEvent(FirebaseEventComment newPost) {
            this.newPost = newPost;
        }

        public FirebaseEventComment getPost() {
            return newPost;
        }
    }

}
