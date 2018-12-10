import React from 'react';
import {Link, withRouter} from 'react-router-dom';
import PropTypes from 'prop-types';
import {connect} from 'react-redux';
import {logOut} from "../../actions/user-actions";
/**
 * Partially taken from
 * https://github.com/arcuri82/pg6300/blob/master/les11/connect4-v2/src/client/headerbar.jsx
 */

class HeaderBar extends React.Component {
   renderLoggedIn() {
       return (
           <div className="msgDiv">
               <h3 className="notLoggedInMsg">Welcome, {this.props.userId}</h3>
               <div className="btn btnPartHeader" onClick={this.props.doLogOut}>
                   Logout
               </div>
           </div>
       );
   }

   renderNotLoggedIn() {
       return (
           <div className="msgDiv">
               <div className="notLoggedInMsg">You are not logged in!</div>
               <div className="btnPartHeader">
                   <Link className="btn" to="/login">Log in</Link>
                   <br/>
                   <Link className="btn" to="/signup">Sign up</Link>
               </div>
           </div>
       );
   }

   render() {
       let content;
       if (this.props.userId == null || !this.props.authenticated) {
           content = this.renderNotLoggedIn();
       } else {
           content = this.renderLoggedIn();
       }

       return (
           <div className="headerBar">
               <Link className="btn home" to={"/"}>Home</Link>
               {content}
           </div>
       );
   }
}

HeaderBar.propTypes = {
    authenticated: PropTypes.bool,
    userId: PropTypes.string,
    doLogOut: PropTypes.func
};

function mapStateToProps(state) {
    return {
        authenticated: state.user.authenticated,
        userId: state.user.userId
    };
}

const mapDispatchToProps = (dispatch) => {
    return {
        doLogOut: () => {
            dispatch(logOut());
        }
    };
};

export default withRouter(connect(mapStateToProps, mapDispatchToProps)(HeaderBar));