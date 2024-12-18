package com.example.demo.service;

import com.example.demo.mapper.FavoriteMapper;
import com.example.demo.model.Book;
import com.example.demo.model.Favorite;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class FavoriteService {

    @Autowired
    private FavoriteMapper favoriteMapper;

    public List<Book> getFavoriteBooks(Long userId) {
        return favoriteMapper.findFavoriteBooks(userId);
    }

    public List<Favorite> getRecentFavorites(Long userId, int limit) {
        return favoriteMapper.findRecentFavorites(userId, limit);
    }

    @Transactional
    public void addFavorite(Long userId, Long bookId) {
        if (!favoriteMapper.isFavorite(userId, bookId)) {
            Favorite favorite = new Favorite();
            favorite.setUserId(userId);
            favorite.setBookId(bookId);
            favoriteMapper.insert(favorite);
        }
    }

    @Transactional
    public void removeFavorite(Long userId, Long bookId) {
        favoriteMapper.deleteByUserAndBook(userId, bookId);
    }

    public boolean isFavorite(Long userId, Long bookId) {
        return favoriteMapper.isFavorite(userId, bookId);
    }

    public int getFavoriteCount(Long bookId) {
        return favoriteMapper.getFavoriteCount(bookId);
    }

    public List<Object[]> getMostFavoritedBooks(int limit) {
        return favoriteMapper.findMostFavoritedBooks(limit);
    }
}
