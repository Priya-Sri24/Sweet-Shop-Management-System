import React, { useState } from 'react';
import {
  TextField,
  Button,
  Paper,
  Grid,
  MenuItem,
} from '@mui/material';
import SearchIcon from '@mui/icons-material/Search';
import ClearIcon from '@mui/icons-material/Clear';

const SearchBar = ({ onSearch, onClear }) => {
  const [filters, setFilters] = useState({
    name: '',
    category: '',
    minPrice: '',
    maxPrice: '',
  });

  const categories = ['Sweet', 'Cake', 'Cookie', 'Candy', 'Chocolate', 'Ice Cream', 'Pastry'];

  const handleChange = (e) => {
    setFilters({
      ...filters,
      [e.target.name]: e.target.value,
    });
  };

  const handleSearch = () => {
    onSearch(filters);
  };

  const handleClear = () => {
    setFilters({
      name: '',
      category: '',
      minPrice: '',
      maxPrice: '',
    });
    onClear();
  };

  return (
    <Paper elevation={3} sx={{ p: 3, mb: 4, borderRadius: 3 }}>
      <Grid container spacing={2} alignItems="center">
        <Grid item xs={12} sm={6} md={3}>
          <TextField
            fullWidth
            label="Search by Name"
            name="name"
            value={filters.name}
            onChange={handleChange}
            variant="outlined"
          />
        </Grid>

        <Grid item xs={12} sm={6} md={2}>
          <TextField
            fullWidth
            select
            label="Category"
            name="category"
            value={filters.category}
            onChange={handleChange}
            variant="outlined"
          >
            <MenuItem value="">All Categories</MenuItem>
            {categories.map((cat) => (
              <MenuItem key={cat} value={cat}>
                {cat}
              </MenuItem>
            ))}
          </TextField>
        </Grid>

        <Grid item xs={6} sm={6} md={2}>
          <TextField
            fullWidth
            label="Min Price"
            name="minPrice"
            type="number"
            value={filters.minPrice}
            onChange={handleChange}
            variant="outlined"
          />
        </Grid>

        <Grid item xs={6} sm={6} md={2}>
          <TextField
            fullWidth
            label="Max Price"
            name="maxPrice"
            type="number"
            value={filters.maxPrice}
            onChange={handleChange}
            variant="outlined"
          />
        </Grid>

        <Grid item xs={6} sm={6} md={1.5}>
          <Button
            fullWidth
            variant="contained"
            startIcon={<SearchIcon />}
            onClick={handleSearch}
            sx={{ height: 56 }}
          >
            Search
          </Button>
        </Grid>

        <Grid item xs={6} sm={6} md={1.5}>
          <Button
            fullWidth
            variant="outlined"
            startIcon={<ClearIcon />}
            onClick={handleClear}
            sx={{ height: 56 }}
          >
            Clear
          </Button>
        </Grid>
      </Grid>
    </Paper>
  );
};

export default SearchBar;