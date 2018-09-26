package net.dunrou.mobile.base.message;

import net.dunrou.mobile.base.SuggestedUser;
import net.dunrou.mobile.base.firebaseClass.FirebaseRelationship;
import net.dunrou.mobile.base.firebaseClass.FirebaseUser;

import java.util.ArrayList;
import java.util.HashMap;

public class DiscoverMessage {
    public static class RelationAddedEvent {
        private final FirebaseRelationship firebaseRelationship;

        public RelationAddedEvent(FirebaseRelationship firebaseRelationship) {
            this.firebaseRelationship = firebaseRelationship;
        }

        public FirebaseRelationship getFirebaseRelationship() {
            return firebaseRelationship;
        }
    }

    public static class RelationAddFailEvent {}

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

    public static class UserAddedEvent {

        private final FirebaseUser firebaseUser;

        public UserAddedEvent(FirebaseUser firebaseUser) {
            this.firebaseUser = firebaseUser;
        }

        public FirebaseUser getFirebaseUser() {
            return firebaseUser;
        }
    }

    public static class UserChangedEvent {

        private final FirebaseUser firebaseUser;

        public UserChangedEvent(FirebaseUser firebaseUser) {
            this.firebaseUser = firebaseUser;
        }

        public FirebaseUser getFirebaseUser() {
            return firebaseUser;
        }
    }

    public static class UserRemovedEvent {

        private final FirebaseUser firebaseUser;

        public UserRemovedEvent(FirebaseUser firebaseUser) {
            this.firebaseUser = firebaseUser;
        }

        public FirebaseUser getFirebaseUser() {
            return firebaseUser;
        }
    }
}
