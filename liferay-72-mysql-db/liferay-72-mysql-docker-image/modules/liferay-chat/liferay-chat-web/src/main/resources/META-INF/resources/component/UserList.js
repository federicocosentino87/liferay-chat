import React from 'react';
import {Grid} from '@material-ui/core';

class UserList extends React.Component {
	
	render() {
		const userList = this.props.userList.map((user) => {
			return (
				<p>{user.userId} - {user.fullName}</p>
			);
		});
		
		return (
			<div className='user-list'>
				{userList}
			</div>
		);
	}
}

export default UserList;