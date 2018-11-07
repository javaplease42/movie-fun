package org.superbiz.moviefun;

import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Controller;
import org.springframework.transaction.TransactionStatus;
import org.springframework.transaction.support.TransactionCallback;
import org.springframework.transaction.support.TransactionOperations;
import org.springframework.web.bind.annotation.GetMapping;
import org.superbiz.moviefun.albums.Album;
import org.superbiz.moviefun.albums.AlbumFixtures;
import org.superbiz.moviefun.albums.AlbumsBean;
import org.superbiz.moviefun.movies.Movie;
import org.superbiz.moviefun.movies.MovieFixtures;
import org.superbiz.moviefun.movies.MoviesBean;

import java.util.Map;

@Controller
public class HomeController {

    private final MoviesBean moviesBean;
    private final AlbumsBean albumsBean;
    private final MovieFixtures movieFixtures;
    private final AlbumFixtures albumFixtures;
    private final TransactionOperations albumsTransactionOperations;
    private final TransactionOperations movieTransactionOperations;

    public HomeController(MoviesBean moviesBean, AlbumsBean albumsBean, MovieFixtures movieFixtures, AlbumFixtures albumFixtures,
                          @Qualifier("albumsTransactionOperations") TransactionOperations albumsTransactionOperations,  @Qualifier("moviesTransactionOperations")  TransactionOperations moviesTransactionOperations) {
        this.moviesBean = moviesBean;
        this.albumsBean = albumsBean;
        this.movieFixtures = movieFixtures;
        this.albumFixtures = albumFixtures;
        this.albumsTransactionOperations = albumsTransactionOperations;
        this.movieTransactionOperations = moviesTransactionOperations;
    }

    @GetMapping("/")
    public String index() {
        return "index";
    }

    @GetMapping("/setup")
    public String setup(Map<String, Object> model) {
        for (Movie movie : movieFixtures.load()) {

            movieTransactionOperations.execute(new TransactionCallback<Object>() {
                @Override
                public Object doInTransaction(TransactionStatus status) {
                    moviesBean.addMovie(movie);
                    return null;
                }
            });

        }

        for (Album album : albumFixtures.load()) {

            albumsTransactionOperations.execute(new TransactionCallback() {
                @Override
                public Object doInTransaction(TransactionStatus status) {
                    albumsBean.addAlbum(album);
                    return null;
                }
            });
        }

        model.put("movies", moviesBean.getMovies());
        model.put("albums", albumsBean.getAlbums());

        return "setup";
    }
}
