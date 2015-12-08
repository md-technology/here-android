package com.mdtech.social.api.json;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class UserResponse extends AbstractResponse {

    @JsonProperty("photo_info")
    private PhotoInfo photoInfo;

	private Collection<Photo> photos;

    @JsonProperty("open_info")
	private User openInfo;
	
	private UserSettings settings;
	
	private List<Circle> circles;
	
	private Circle circle;
	
	private List<User> followers;
	
	// 系统推荐关注
	private List<User> follow;

    public static class PhotoInfo {

        public PhotoInfo() {
            super();
        }

        public PhotoInfo(Long photoCount) {
            this.photoCount = photoCount;
        }

        public PhotoInfo(Long photoCount, int photoNum) {
            this.photoCount = photoCount;
            this.photoNum = photoNum;
        }

        @JsonProperty("photo_count")
        private Long photoCount;

        @JsonProperty("photo_num")
        private int photoNum;

        public Long getPhotoCount() {
            return photoCount;
        }

        public void setPhotoCount(Long photoCount) {
            this.photoCount = photoCount;
        }

        public int getPhotoNum() {
            return photoNum;
        }

        public void setPhotoNum(int photoNum) {
            this.photoNum = photoNum;
        }
    }

	public static class Circle {
		private Long id;
		private String name;
		private Set<User> users = new HashSet<User>(0);
		public Long getId() {
			return id;
		}
		public void setId(Long id) {
			this.id = id;
		}
		public String getName() {
			return name;
		}
		public void setName(String name) {
			this.name = name;
		}
		public Set<User> getUsers() {
			return users;
		}
		public void setUsers(Set<User> users) {
			this.users = users;
		}
	}

    public Collection<Photo> getPhotos() {
        return photos;
    }

    public void setPhotos(Collection<Photo> photos) {
        this.photos = photos;
    }

    public User getOpenInfo() {
        return openInfo;
    }

    public void setOpenInfo(User openInfo) {
        this.openInfo = openInfo;
    }

    public UserSettings getSettings() {
        return settings;
    }

    public void setSettings(UserSettings settings) {
        this.settings = settings;
    }

    public List<Circle> getCircles() {
        return circles;
    }

    public void setCircles(List<Circle> circles) {
        this.circles = circles;
    }

    public Circle getCircle() {
        return circle;
    }

    public void setCircle(Circle circle) {
        this.circle = circle;
    }

    public List<User> getFollowers() {
        return followers;
    }

    public void setFollowers(List<User> followers) {
        this.followers = followers;
    }

    public List<User> getFollow() {
        return follow;
    }

    public void setFollow(List<User> follow) {
        this.follow = follow;
    }
}
