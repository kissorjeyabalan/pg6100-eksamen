import React from 'react';
import {Link, withRouter} from 'react-router-dom';
import {connect} from 'react-redux';
import {axios, MOVIE_API, SHOW_API, THEATER_API} from "../../global";

class AdminEditShowPage extends React.Component {

    constructor(props) {
        super(props)

        this.state = {
            show: {releaseDate: ""},
            movie: {title: ""},
            cinema: {name: ""},
            error: null,
            title: "",
            theater: "",
            releaseDate: ""
        };

        this.onReleasedateChange = this.onReleasedateChange.bind(this);
        this.saveShow = this.saveShow.bind(this);

        this.show = new URLSearchParams(window.location.search).get("show");

        if (this.show === null || this.show === undefined) {
            this.state.error = "Unspecified show";
        }
    }

    componentWillMount() {
        axios.get(`${SHOW_API}/${this.props.match.params.id}`).then(res => {
            let show = res.data.data;
            axios.get(`${MOVIE_API}/${show.movieId}`).then(res => {
                let movie = res.data.data;
                axios.get(`${THEATER_API}/${show.cinemaId}`).then(res => {
                    let cinema = res.data.data;
                    this.setState({
                        cinema: cinema,
                        show: show,
                        movie: movie,
                        title: movie.title,
                        theater: cinema.name,
                        releaseDate: movie.release_date
                    })
                })
            });
        });
    }

    saveShow() {
        axios.patch(`${SHOW_API}/{this.props.match.params.id}`, {startTime: this.state.releaseDate}).then(res => {
            alert("successfuly updated")
        }).catch(() => {
           alert("failed to update")
        });
    }

    onReleasedateChange(event) {
        this.setState({releaseDate: event.target.value});
    }

    render() {

        let movie = <div>Loading movie</div>

        if(this.state.movie !== null) {
            movie = <div className="movie-container">
                <h2>Title: {this.state.title}</h2>
                <p>Theater name: {this.state.theater}</p>
            </div>
        }

        return(
            <div className="container">
                <h2>Movie-Detail-Page</h2>
                {movie}
                <br/>
                <br/>
                <br/>
                <br/>
                <br/>
                <div>
                    <div>
                        <div>
                            <div className="input-time">New time:</div>
                            <input
                                type="text"
                                placeholder={"Type the release date of this movie"}
                                value={this.state.releaseDate}
                                onChange={this.onReleasedateChange}
                                className={"movie-input"}
                                id={"releasedate-input"}
                            />
                        </div>
                        <div>
                            <button className="btn black">
                                Save
                            </button>
                        </div>
                    </div>
                </div>
            </div>
        );
    }
}

function mapStateToProps(state) {
    return {

    };
}

export default withRouter(connect(mapStateToProps)(AdminEditShowPage))

