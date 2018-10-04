package net.dunrou.mobile.base.message;

import net.dunrou.mobile.base.firebaseClass.FirebaseEventPost;
import net.dunrou.mobile.base.firebaseClass.FirebaseUser;

public class ProfileMessage {
    public static class RefreshProfile {
        private FirebaseUser firebaseUser;

        public RefreshProfile(FirebaseUser firebaseUser) {
            this.firebaseUser = firebaseUser;
        }

        public FirebaseUser getProfile() {
            return firebaseUser;
        }
    }

    public static class UpdateNumOfPosts {
        private int posts;

        public UpdateNumOfPosts(int posts) {
            this.posts = posts;
        }

        public int getPosts() {
            return posts;
        }
    }

    public static class UpdateNumOfFollowers {
        private int followers;

        public UpdateNumOfFollowers(int followers) {
            this.followers = followers;
        }

        public int getFollowers() {
            return followers;
        }
    }

    public static class UpdateNumOfFollowing {
        private int following;

        public UpdateNumOfFollowing(int following) {
            this.following = following;
        }

        public int getFollowing() {
            return following;
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


}
