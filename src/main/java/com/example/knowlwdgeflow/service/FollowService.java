package com.example.knowlwdgeflow.service;

import com.example.knowlwdgeflow.dao.FollowDao;

public class FollowService {
    private final FollowDao followDao = new FollowDao();

    public boolean toggleFollow(int followerId, int followeeId) throws Exception {
        if (followerId == followeeId) {
            return false; // cannot follow yourself
        }
        boolean isFollowing = followDao.isFollowing(followerId, followeeId);
        if (isFollowing) {
            followDao.unfollow(followerId, followeeId);
            return false;
        } else {
            followDao.follow(followerId, followeeId);
            return true;
        }
    }

    public boolean isFollowing(int followerId, int followeeId) throws Exception {
        if (followerId == followeeId) {
            return false;
        }
        return followDao.isFollowing(followerId, followeeId);
    }

    public int countFollowers(int userId) throws Exception {
        return followDao.countFollowers(userId);
    }
}

