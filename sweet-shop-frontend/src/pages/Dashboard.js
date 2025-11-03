import React, { useState, useEffect } from 'react';
import {
  Container,
  Grid,
  Typography,
  Box,
  CircularProgress,
  Alert,
} from '@mui/material';
import SweetCard from '../components/SweetCard';
import SearchBar from '../components/SearchBar';
import { sweetService } from '../services/authService';
import { toast } from 'react-toastify';

const Dashboard = () => {
  const [sweets, setSweets] = useState([]);
  const [loading, setLoading] = useState(true);
  const [searchActive, setSearchActive] = useState(false);

  const fetchSweets = async () => {
    setLoading(true);
    try {
      const response = await sweetService.getAllSweets();
      setSweets(response.data || []);
    } catch (error) {
      toast.error('Failed to fetch sweets');
    } finally {
      setLoading(false);
    }
  };

  useEffect(() => {
    fetchSweets();
  }, []);

  const handleSearch = async (filters) => {
    setLoading(true);
    setSearchActive(true);
    try {
      const params = {};
      if (filters.name) params.name = filters.name;
      if (filters.category) params.category = filters.category;
      if (filters.minPrice) params.minPrice = filters.minPrice;
      if (filters.maxPrice) params.maxPrice = filters.maxPrice;

      const response = await sweetService.searchSweets(params);
      setSweets(response.data || []);
      toast.success(`Found ${response.data?.length || 0} sweets`);
    } catch (error) {
      toast.error('Search failed');
    } finally {
      setLoading(false);
    }
  };

  const handleClearSearch = () => {
    setSearchActive(false);
    fetchSweets();
  };

  return (
    <Container maxWidth="xl" sx={{ mt: 4, mb: 4 }}>
      <Box sx={{ mb: 4 }}>
        <Typography
          variant="h3"
          fontWeight="bold"
          align="center"
          sx={{
            background: 'linear-gradient(45deg, #FF6B6B 30%, #FFE66D 90%)',
            WebkitBackgroundClip: 'text',
            WebkitTextFillColor: 'transparent',
            mb: 1,
          }}
        >
          🍰 Sweet Shop Dashboard 🍰
        </Typography>
        <Typography variant="h6" align="center" color="text.secondary">
          Explore our delicious collection of sweets!
        </Typography>
      </Box>

      <SearchBar onSearch={handleSearch} onClear={handleClearSearch} />

      {loading ? (
        <Box display="flex" justifyContent="center" alignItems="center" minHeight="400px">
          <CircularProgress size={60} />
        </Box>
      ) : sweets.length === 0 ? (
        <Alert severity="info" sx={{ mt: 4 }}>
          {searchActive
            ? 'No sweets found matching your search criteria. Try adjusting your filters!'
            : 'No sweets available at the moment. Check back later!'}
        </Alert>
      ) : (
        <>
          <Typography variant="h6" sx={{ mb: 2 }}>
            {searchActive ? `Search Results (${sweets.length})` : `All Sweets (${sweets.length})`}
          </Typography>
          <Grid container spacing={3}>
            {sweets.map((sweet) => (
              <Grid item xs={12} sm={6} md={4} lg={3} key={sweet.id}>
                <SweetCard sweet={sweet} onPurchaseSuccess={fetchSweets} />
              </Grid>
            ))}
          </Grid>
        </>
      )}
    </Container>
  );
};

export default Dashboard;