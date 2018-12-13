import React from 'react';
import {Link, withRouter} from 'react-router-dom';
import {connect} from 'react-redux';
import axios from "axios/index";
import * as ApiBase from "../../global";

class MoviePage extends React.Component {

    constructor(props) {
        super(props)

        this.state = {
            movie: null,
            screenings: null,
            error: null
        };

        this.movieId = new URLSearchParams(window.location.search).get("movieId");

        if(this.movieId === null){
            this.state.error = "Unspecified book id";
        }

    }

    componentDidMount() {
       /*
        axios.get(`${ApiBase.MOVIE_API}/${this.movieId}`).then(res => {

            let payload = res.data.data
            console.log(payload)
            this.setState({movie: payload})
        })

        axios.get(`${ApiBase.KINO_API}/${this.movieId}`).then(res => {

            let payload = res.data.data
            console.log(payload)
            this.setState({screenings: payload})
        })
        */
        let fakeMovieReply = {
            data:
                {
                    id: 1,
                    title: "he-man",
                    release_date:"dadddwadwa",
                    description: "here is a loooooooooong descrda daw  dawd awdwad  d wd a w wd awd  dw"
                }
        }
        this.setState({
            movie: fakeMovieReply.data
        })

        let fakeScreeningReply = {
            data:
                [
                    {
                        startTime: "19:00",
                        movieId: "1",
                        cinemaId:"2",
                        availableSeats: "10 seats",
                        id: 1
                    },
                    {
                        startTime: "20:00",
                        movieId: "1",
                        cinemaId:"2",
                        availableSeats: "15 seats",
                        id: 2
                    }
                ]

        }
        this.setState({
            movie: fakeScreeningReply.data
        })

    }


    render() {

        let movie = <div>Loading</div>

        if(this.state.movie !== null) {
            movie = <div className="movie-container">
                <h2>{this.state.movie.title}</h2>

                <p>Release Date: {this.state.movie.release_date}</p>
                <p>{this.state.movie.description}</p>
            </div>
        }

        let screenings = <div>Loading</div>

        if(this.state.screenings !== null) {
            movie = <div className="container">
                {this.state.screenings.map( s =>
                    <div key={s.id} className="movie-list-card">
                        <p>{s.startTime}</p>
                        <p>{s.availableSeats}</p>
                    </div>

                )}
            </div>
        }

        return(
            <div className="container">
                <h2>Movie-Detail-Page</h2>
                {movie}
            </div>
        );
    }
}



function mapStateToProps(state) {
    return {

    };
}

export default withRouter(connect(mapStateToProps)(MoviePage))