import React from 'react';
import {Link, withRouter} from 'react-router-dom';
import PropTypes from 'prop-types';
import {signUp} from "../../actions/user-actions";
import {connect} from 'react-redux';

class RegisterPage extends React.Component {
    constructor(props) {
        super(props);

        this.state = {
            userId: "",
            password: ""
        };

        this.onUserIdChange = this.onUserIdChange.bind(this);
        this.onPasswordChange = this.onPasswordChange.bind(this);
        this.signUp = this.signUp.bind(this);
    }

    onUserIdChange(event) {
        this.setState({userId: event.target.value});
    }

    onPasswordChange(event) {
        this.setState({password: event.target.value});
    }

    signUp() {
        if (this.props.authenticated && this.props.history != null) {
            this.props.history.push('/');
        }
        this.props.signUp(this.state.userId, this.state.password, this.props.history);
    }

    render() {
        let error;
        if (this.props.error != null) {
            error = <div><p>{this.props.error}</p></div>
        }

        return(
            <div className="container">
                <h1>Sign up</h1>
                <div>
                    <p>Username: </p>
                    <input type="text"
                           value={this.state.userId}
                           onChange={this.onUserIdChange}/>
                </div>
                <div>
                    <p>Password: </p>
                <input type="password"
                       value={this.state.password}
                       onChange={this.onPasswordChange}/>
                </div>
                {error}
                <div className="btn" onClick={this.signUp}>Sign up!</div>
                <Link to={"/login"}>Click here if you're already registered!</Link>
            </div>
        );
    }
}

RegisterPage.propTypes = {
    signUp: PropTypes.func.isRequired,
    error: PropTypes.string,
    history: PropTypes.object,
    authenticated: PropTypes.bool
};

function mapStateToProps(state) {
    return {
        error: state.user.error,
        authenticated: state.user.authenticated
    };
}

const mapDispatchToProps = (dispatch) => {
    return {
        signUp: (username, password, history) => {
            dispatch(signUp(username, password, history));
        }
    };
};

export default withRouter(connect(mapStateToProps, mapDispatchToProps)(RegisterPage));