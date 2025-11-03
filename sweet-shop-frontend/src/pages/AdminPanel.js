import React, { useState, useEffect } from 'react';
import {
  Container,
  Typography,
  Button,
  Box,
  Table,
  TableBody,
  TableCell,
  TableContainer,
  TableHead,
  TableRow,
  Paper,
  IconButton,
  Chip,
  Dialog,
  DialogTitle,
  DialogContent,
  DialogActions,
  TextField,
  CircularProgress,
} from '@mui/material';
import AddIcon from '@mui/icons-material/Add';
import EditIcon from '@mui/icons-material/Edit';
import DeleteIcon from '@mui/icons-material/Delete';
import InventoryIcon from '@mui/icons-material/Inventory';
import SweetForm from '../components/SweetForm';
import { sweetService } from '../services/authService';
import { toast } from 'react-toastify';

const AdminPanel = () => {
  const [sweets, setSweets] = useState([]);
  const [loading, setLoading] = useState(true);
  const [formOpen, setFormOpen] = useState(false);
  const [selectedSweet, setSelectedSweet] = useState(null);
  const [restockDialog, setRestockDialog] = useState({ open: false, sweet: null });
  const [restockQuantity, setRestockQuantity] = useState('');

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

  const handleAddSweet = () => {
    setSelectedSweet(null);
    setFormOpen(true);
  };

  const handleEditSweet = (sweet) => {
    setSelectedSweet(sweet);
    setFormOpen(true);
  };

  const handleDeleteSweet = async (id) => {
    if (window.confirm('Are you sure you want to delete this sweet?')) {
      try {
        await sweetService.deleteSweet(id);
        toast.success('Sweet deleted successfully!');
        fetchSweets();
      } catch (error) {
        toast.error(error.response?.data?.message || 'Delete failed');
      }
    }
  };

  const handleRestockOpen = (sweet) => {
    setRestockDialog({ open: true, sweet });
    setRestockQuantity('');
  };

  const handleRestockClose = () => {
    setRestockDialog({ open: false, sweet: null });
    setRestockQuantity('');
  };

  const handleRestock = async () => {
    const quantity = parseInt(restockQuantity);
    if (!quantity || quantity < 1) {
      toast.error('Please enter a valid quantity');
      return;
    }

    try {
      await sweetService.restockSweet(restockDialog.sweet.id, quantity);
      toast.success(`Restocked ${quantity} items successfully!`);
      handleRestockClose();
      fetchSweets();
    } catch (error) {
      toast.error(error.response?.data?.message || 'Restock failed');
    }
  };

  return (
    <Container maxWidth="xl" sx={{ mt: 4, mb: 4 }}>
      <Box sx={{ mb: 4, display: 'flex', justifyContent: 'space-between', alignItems: 'center' }}>
        <Typography variant="h4" fontWeight="bold">
          🛠️ Admin Panel
        </Typography>
        <Button
          variant="contained"
          startIcon={<AddIcon />}
          onClick={handleAddSweet}
          sx={{
            background: 'linear-gradient(45deg, #FF6B6B 30%, #FFE66D 90%)',
            fontWeight: 'bold',
          }}
        >
          Add New Sweet
        </Button>
      </Box>

      {loading ? (
        <Box display="flex" justifyContent="center" alignItems="center" minHeight="400px">
          <CircularProgress size={60} />
        </Box>
      ) : (
        <TableContainer component={Paper} elevation={3}>
          <Table>
            <TableHead>
              <TableRow sx={{ bgcolor: '#f5f5f5' }}>
                <TableCell><strong>ID</strong></TableCell>
                <TableCell><strong>Name</strong></TableCell>
                <TableCell><strong>Category</strong></TableCell>
                <TableCell><strong>Price</strong></TableCell>
                <TableCell><strong>Quantity</strong></TableCell>
                <TableCell><strong>Description</strong></TableCell>
                <TableCell align="center"><strong>Actions</strong></TableCell>
              </TableRow>
            </TableHead>
            <TableBody>
              {sweets.map((sweet) => (
                <TableRow key={sweet.id} hover>
                  <TableCell>{sweet.id}</TableCell>
                  <TableCell>
                    <Typography fontWeight="medium">{sweet.name}</Typography>
                  </TableCell>
                  <TableCell>
                    <Chip label={sweet.category} size="small" color="primary" variant="outlined" />
                  </TableCell>
                  <TableCell>
                    <Typography fontWeight="bold" color="primary">
                      ${sweet.price}
                    </Typography>
                  </TableCell>
                  <TableCell>
                    <Chip
                      label={sweet.quantity}
                      size="small"
                      color={sweet.quantity > 10 ? 'success' : sweet.quantity > 0 ? 'warning' : 'error'}
                    />
                  </TableCell>
                  <TableCell sx={{ maxWidth: 200 }}>
                    <Typography variant="body2" noWrap>
                      {sweet.description || 'No description'}
                    </Typography>
                  </TableCell>
                  <TableCell align="center">
                    <IconButton
                      color="primary"
                      onClick={() => handleEditSweet(sweet)}
                      title="Edit"
                    >
                      <EditIcon />
                    </IconButton>
                    <IconButton
                      color="success"
                      onClick={() => handleRestockOpen(sweet)}
                      title="Restock"
                    >
                      <InventoryIcon />
                    </IconButton>
                    <IconButton
                      color="error"
                      onClick={() => handleDeleteSweet(sweet.id)}
                      title="Delete"
                    >
                      <DeleteIcon />
                    </IconButton>
                  </TableCell>
                </TableRow>
              ))}
            </TableBody>
          </Table>
        </TableContainer>
      )}

      <SweetForm
        open={formOpen}
        onClose={() => setFormOpen(false)}
        sweet={selectedSweet}
        onSuccess={fetchSweets}
      />

      <Dialog open={restockDialog.open} onClose={handleRestockClose}>
        <DialogTitle>Restock {restockDialog.sweet?.name}</DialogTitle>
        <DialogContent>
          <Box sx={{ pt: 2 }}>
            <Typography variant="body2" sx={{ mb: 2 }}>
              Current Stock: {restockDialog.sweet?.quantity}
            </Typography>
            <TextField
              autoFocus
              fullWidth
              label="Quantity to Add"
              type="number"
              value={restockQuantity}
              onChange={(e) => setRestockQuantity(e.target.value)}
              inputProps={{ min: 1 }}
            />
          </Box>
        </DialogContent>
        <DialogActions>
          <Button onClick={handleRestockClose}>Cancel</Button>
          <Button onClick={handleRestock} variant="contained">
            Restock
          </Button>
        </DialogActions>
      </Dialog>
    </Container>
  );
};

export default AdminPanel;