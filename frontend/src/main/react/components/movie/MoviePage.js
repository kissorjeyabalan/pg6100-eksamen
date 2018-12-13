import React from 'react';
import {Link, withRouter} from 'react-router-dom';
import {connect} from 'react-redux';
import {MOVIE_API, SHOW_API, axios, ORDER_API, THEATER_API} from "../../global";

class MoviePage extends React.Component {

    constructor(props) {
        super(props)

        this.state = {
            movie: null,
            screenings: null,
            error: null
        };

        this.movieId = new URLSearchParams(window.location.search).get("movieId");
        this.reserveSeat = this.reserveSeat.bind(this);

        if(this.movieId === null){
            this.state.error = "Unspecified movie id";
        }
    }

    componentDidMount() {

        axios.get(`${MOVIE_API}/${this.movieId}`).then(res => {
            let payload = res.data.data
            this.setState({movie: payload})
        })

        axios.get(`${SHOW_API}?movieId=${this.movieId}`).then(res => {
            let payload = res.data.data.data;
            let screenings = [];

            payload.forEach(show => {
                show = {...show, reservedSeats: []};
                axios.get(`${THEATER_API}/${show.cinemaId}`).then(res => {

                    show = {...show, theaterName: res.data.data.name};
                    screenings.push(show)
                    this.setState({screenings: screenings})

                }).catch(err => {
                    console.log(err)
                })
            })
        })
    }

    placeOrder(screeningId, screeningIndex) {
        let obj = {
            "screening_id": screeningId.toString(),
            "payment_token": "234adsf",
            "seats": this.state.screenings[screeningIndex].reservedSeats
        }

        // place order, uncomment if spring security works
        /*axios.post(`${ORDER_API}`, obj).then(res => {
            alert("Order successfully placed")
        }).catch(err => {
            alert("Something went wrong. You've been refunded.")
        })*/
    }


    reserveSeat(selectedSeat, screeningId, screeningIndex) {


        let resSeats = this.state.screenings[screeningIndex].reservedSeats;
        let index = resSeats.indexOf(selectedSeat);

        if (index > -1) {
            resSeats.push(selectedSeat);
        } else {
            resSeats.splice(index, 1)
        }
        this.setState({reservedSeats: resSeats})

        // IF SPRING-SECURITY WORKED, CONTENT OF THIS METHOD WOULD GO INSIDE THIS

        /*axios.post(`${ORDER_API}/reserve`, {"seat": selectedSeat, "screening_id": screeningId}).then(res => {
            if (res.status === 204) {
                // add to list in state
            }
        }).catch(err => {
            alert("Seat is already taken by another customer, please choose another seat")
        })*/
    }


    render() {

        let movie = <div>Loading movie</div>

        if(this.state.movie !== null) {
            movie = <div className="movie-container">
                <h2>{this.state.movie.title}</h2>

                <p>Release Date: {this.state.movie.release_date}</p>
                <p>{this.state.movie.description}</p>
            </div>
        }

        let screenings = <div>Loading screenings</div>

        if(this.state.screenings !== null) {
            screenings = <div>
                {this.state.screenings.map((s, screeningIndex) =>
                    <div key={s.id} className="screening-card">
                        <h3>Theater: {s.theaterName}</h3>
                        <p>Start-time: {s.startTime}</p>
                        <p>Available Seats:</p>
                        {s.availableSeats.map((seat, index) =>
                            <button className="available-seat" key={index} onClick={() => this.reserveSeat(seat, s.id, screeningIndex)}>{seat}</button>
                        )}
                        <p>Reserverte seter: {s.reservedSeats.map(rs =>
                            <p>{rs}</p>
                        )}</p>
                        <button className={"btn"} onClick={() => this.placeOrder(s.id, screeningIndex)}>PLACE ORDER</button>
                    </div>
                )}
            </div>
        }

        return(
            <div className="container">
                <h2>Movie-Detail-Page</h2>
                {movie}
                {screenings}
            </div>
        );
    }
}


function mapStateToProps(state) {
    return {

    };
}

export default withRouter(connect(mapStateToProps)(MoviePage))