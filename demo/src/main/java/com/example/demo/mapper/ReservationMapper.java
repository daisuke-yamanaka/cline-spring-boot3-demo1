package com.example.demo.mapper;

import com.example.demo.model.Reservation;
import org.apache.ibatis.annotations.*;

import java.util.List;

@Mapper
public interface ReservationMapper {
    @Select("SELECT * FROM reservations WHERE reservation_id = #{reservationId}")
    Reservation findById(Long reservationId);

    @Select("SELECT * FROM reservations WHERE book_id = #{bookId}")
    List<Reservation> findByBookId(Long bookId);

    @Select("SELECT * FROM reservations WHERE user_id = #{userId}")
    List<Reservation> findByUserId(Long userId);

    @Select("SELECT * FROM reservations WHERE status = #{status}")
    List<Reservation> findByStatus(Integer status);

    @Insert("INSERT INTO reservations (book_id, user_id, status) " +
            "VALUES (#{bookId}, #{userId}, #{status})")
    @Options(useGeneratedKeys = true, keyProperty = "reservationId")
    int insert(Reservation reservation);

    @Update("UPDATE reservations SET status = #{status} " +
            "WHERE reservation_id = #{reservationId}")
    int updateStatus(@Param("reservationId") Long reservationId, 
                    @Param("status") Integer status);

    @Delete("DELETE FROM reservations WHERE reservation_id = #{reservationId}")
    int delete(Long reservationId);

    @Select("SELECT COUNT(*) FROM reservations " +
            "WHERE book_id = #{bookId} AND status = 0")
    int getActiveReservationCount(Long bookId);

    @Select("SELECT * FROM reservations " +
            "WHERE book_id = #{bookId} AND status = 0 " +
            "ORDER BY reservation_date ASC LIMIT 1")
    Reservation getNextReservation(Long bookId);

    @Select("SELECT EXISTS(SELECT 1 FROM reservations " +
            "WHERE book_id = #{bookId} AND user_id = #{userId} AND status = 0)")
    boolean hasActiveReservation(@Param("bookId") Long bookId, 
                                @Param("userId") Long userId);

    @Select("SELECT r.* FROM reservations r " +
            "JOIN books b ON r.book_id = b.book_id " +
            "WHERE b.status = 0 AND r.status = 0 " +
            "ORDER BY r.reservation_date ASC")
    List<Reservation> findReservationsForAvailableBooks();

    @Update("UPDATE reservations SET status = 2 " +
            "WHERE book_id = #{bookId} AND status = 0 " +
            "AND reservation_date < #{expirationDate}")
    int cancelExpiredReservations(@Param("bookId") Long bookId, 
                                 @Param("expirationDate") java.time.LocalDateTime expirationDate);
}
