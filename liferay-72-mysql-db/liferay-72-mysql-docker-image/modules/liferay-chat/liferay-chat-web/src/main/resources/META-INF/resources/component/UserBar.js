import React from 'react';
import {Grid, TextField, Typography} from '@material-ui/core';
import Autocomplete from '@material-ui/lab/Autocomplete';

export default function UserBar(props) {

	const defaultProps = {
		options: props.userList,
		getOptionLabel: (option) => option.fullName,
	};

	const [value, setValue] = React.useState(null);
	
	function handleInputSelection(event, value, reason) {
		if (reason === 'select-option') {
			setValue(null);
		}
		
		props.handleInputSelection(event, value, reason);
	}
	
	return (
		<div className='user-bar'>
			<Grid container>
				<Grid item xs={4}>
					<Typography variant="h6" noWrap>
						Liferay-chat
					</Typography>
				</Grid>
				<Grid item xs={8}>
					<Autocomplete
						{...defaultProps}
						value={value}
						id="auto-complete"
						autoComplete
						onChange={handleInputSelection}
						includeInputInList
						onInputChange={props.handleInputChange}
						renderInput={(params) => <TextField {...params} label="Search..." margin="normal" />}
					/>
				</Grid>
			</Grid>
		</div>
	);
}