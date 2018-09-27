package net.dunrou.mobile.base.message;

import net.dunrou.mobile.base.SuggestedUser;
import net.dunrou.mobile.base.firebaseClass.FirebaseRelationship;
import net.dunrou.mobile.base.firebaseClass.FirebaseUser;

import java.util.ArrayList;

/**
 * Created by yvette on 2018/9/24.
 */

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

        private final SuggestedUser suggestedUser;

        public UserAddedEvent(SuggestedUser suggestedUser) {
            this.suggestedUser = suggestedUser;
        }

        public SuggestedUser getSuggestedUser() {
            return suggestedUser;
        }
    }

    public static class UserChangedEvent {

        private final SuggestedUser suggestedUser;

        public UserChangedEvent(SuggestedUser suggestedUser) {
            this.suggestedUser = suggestedUser;
        }

        public SuggestedUser getSuggestedUser() {
            return suggestedUser;
        }
    }

    public static class UserRemovedEvent {

        private final SuggestedUser suggestedUser;

        public UserRemovedEvent(SuggestedUser suggestedUser) {
            this.suggestedUser = suggestedUser;
        }

        public SuggestedUser getSuggestedUser() {
            return suggestedUser;
        }
    }

    public static class UserSearchGetEvent {
        private final ArrayList<SuggestedUser> suggestedUsers;

        public UserSearchGetEvent(ArrayList<SuggestedUser> suggestedUsers) {
            this.suggestedUsers = suggestedUsers;
        }

        public ArrayList<SuggestedUser> getSuggestedUsers() {
            return suggestedUsers;
        }
    }
}
